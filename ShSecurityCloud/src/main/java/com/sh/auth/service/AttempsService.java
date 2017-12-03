package com.sh.auth.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sh.auth.events.Blacklist;



@Service
public class AttempsService 
{
	@Value("${blacklist.store.path}")
	private String blacklistLocation;
	
	private static final String BLACKLIST = "blacklist.bin";
	
	@Value("${blacklist.max.attempts:4}")
	private Integer maxAtts;
	
	@Value("${blacklist.minutes.banned:3}")
	private Integer minutesLocked;
	
	@Value("${blacklist.warning.brute:15}")
	private Integer bruteForceWarning; 
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AttempsService.class);
	
	private AtomicInteger counter;
	
	private void createDir() throws IOException
	{
		Path bl = new File(blacklistLocation).toPath();
		if(!Files.exists(bl))
		{
			Files.createDirectory(bl);
		}
	}
	
	
	private void updateBlacklist(final Map<String,Blacklist> ls)
	{
		ObjectOutputStream objStream = null;
		
		
		try {
			objStream = new ObjectOutputStream(new FileOutputStream(blacklistLocation+BLACKLIST));
			objStream.writeObject(ls);
		} 
		catch (IOException e) 
		{
			LOGGER.error("Error: ",e);
		}
		finally
		{
			if(objStream != null)
			{
				try 
				{
					objStream.close();
				}
				catch (IOException e) 
				{
					LOGGER.error("Error: ",e);
				}
			}
		}
		
	}
	
	
	
	@SuppressWarnings({ "unused", "unchecked" })
	private Map<String,Blacklist> readBlackList()
	{
		ObjectInputStream objtIstr = null;
		
		Map<String,Blacklist> lsBlk = null;
		try 
		{
			File f = new File(blacklistLocation+BLACKLIST);
			if(f.exists())
			{
				objtIstr = new ObjectInputStream(new FileInputStream(blacklistLocation+BLACKLIST));		
				lsBlk = (Map<String,Blacklist>) objtIstr.readObject();
			}
		} 
		catch (FileNotFoundException e) 
		{
			LOGGER.error("Error: ",e);
		} 
		catch (IOException e) 
		{
			LOGGER.error("Error: ",e);
		}
		catch (ClassNotFoundException e) 
		{
			LOGGER.error("Error: ",e);
		}
		finally
		{
			if(objtIstr != null)
			{
				try 
				{
					objtIstr.close();
					return lsBlk;
				}
				catch (IOException e) 
				{
					LOGGER.error("Error: ",e);
				}
			}
			else
			{
				return lsBlk;
			}
		}
		return lsBlk;
	}


	public void createBlacklist(final List<String> users)
	{
		ObjectOutputStream objStream = null;
		try {
			createDir();
			File f = new File(blacklistLocation+BLACKLIST);
			if(!f.exists())
			{
				final Map<String,Blacklist> mapBlk = new HashMap<String,Blacklist>();
				users.stream().forEach(user -> 
				{
					Blacklist blk = new Blacklist();
					blk.setUser(user);
					mapBlk.put(user, blk);
				});
					
				objStream = new ObjectOutputStream(new FileOutputStream(blacklistLocation+BLACKLIST));
				objStream.writeObject(mapBlk);
			}
			
		} 
		catch (FileNotFoundException e) 
		{
			LOGGER.error("Error: ",e);
		}
		catch (IOException e) 
		{
			LOGGER.error("Error: ",e);
		}
		finally
		{
			if(objStream != null)
			{
				try 
				{
					objStream.close();
				}
				catch (IOException e) 
				{
					LOGGER.error("Error: ",e);
				}
			}
		}		
	}
	
	public Blacklist getUserStatus(String user)
	{
		File f = new File(blacklistLocation+BLACKLIST);
		if(f.exists())
		{
			Map<String,Blacklist> lsBlk = readBlackList();
			Blacklist userSelected = lsBlk.get(user);
			return userSelected;
		}
		else
		{
			return null;
		}
	}


	public void updateUserSuccess(Blacklist usu)
	{
		File f = new File(blacklistLocation+BLACKLIST);
		if(f.exists())
		{
			Map<String,Blacklist> lsBlk = readBlackList();
			lsBlk.put(usu.getUser(), usu);
		
		}
	}
	
	
	public Blacklist attempt(final String user)
	{
		File f = new File(blacklistLocation+BLACKLIST);
		if(f.exists())
		{
			Map<String,Blacklist> lsBlk = readBlackList();
			Blacklist userSelected = lsBlk.get(user);
			if(userSelected != null)
			{
				LOGGER.debug(user.concat(" EXISTS"));
				userSelected = attemptCheck(userSelected);
				lsBlk.put(user, userSelected);
				updateBlacklist(lsBlk);
				return userSelected;
			}
			else
			{
				LOGGER.debug(user.concat(" NOT EXISTS"));
				return null;
			}
			
		}
		else
		{
			return null;
		}
		
		
	}
	
	private Blacklist attemptCheck(Blacklist usu)
	{
		if(!usu.isLocked())
		{
			if(usu.getAtt()>=maxAtts)
			{
				usu.setLocked(true);
				usu.setTimestamp(System.currentTimeMillis());
				LOGGER.debug(usu.getUser().concat(" LOCKED"));
			}
			else
			{
				counter = new AtomicInteger(usu.getAtt());
				usu.setAtt(counter.incrementAndGet());
			}
		}
		else 
		{
			usu = periodCheckLock(usu);
			
		}
		
		return usu;
	}


	public Blacklist periodCheckLock(Blacklist usu) {
		
		DateTime lockedTime = new DateTime(usu.getTimestamp()).plusMinutes(minutesLocked);
		boolean yetLocked = lockedTime.isAfterNow();
			
		if(yetLocked)
		{
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
			LOGGER.debug("LOCKED UNTIL: "+lockedTime.toString(fmt));
			usu.setLocked(true);
			counter = new AtomicInteger(usu.getAtt());
			usu.setAtt(counter.incrementAndGet());
			LOGGER.debug(MessageFormat.format("{0} LOCKED - [ATTEMPTS = {1}]", usu.getUser(),usu.getAtt()));
		}
		else
		{
			if(usu.getAtt()>bruteForceWarning)
				LOGGER.warn(usu.getUser().concat(" UNLOCKED BUT RETRYING.POSSIBLE BRUTE FORCE"));
			
			usu.setLocked(false);
			counter = new AtomicInteger(0);
			usu.setAtt(counter.incrementAndGet());
			usu.setTimestamp(null);
			
		}
		return usu;
	}
	
	
	
}
