

var bootLogin = angular.module("bootLogin",[]);

var authPortal = angular.module("authPortal",[]);


authPortal.controller('authCtrl',['$http','$scope','$interval',function($http,$scope,$interval)
{
	var self = $scope;
	
	self.expiration = 0;
	
	self.remain = 0;
	
	self.expireDate = "";
	
	self.isExpired = false;
	
	self.routes = [];
	
	self.getExpiration = function()
	{
		$http.get("/getSSOnfo").then(function(resp) {
			
			var expire = resp.data;
			
			var current = new Date();
			var currTime = current.getTime();
			
			self.expireDate = moment(expire).format("YYYY-MM-DD HH:mm:ss");
			
			self.expiration = resp.data;
		}, function(error) {
			
		});
	};
	
	self.getRoutes = function()
	{
		$http.get("/routes").then(function(resp) 
		{
			var routes = resp.data;
			
			for(var key in routes)
			{
				if(routes.hasOwnProperty(key))
				{
					var s = key.substring(0, key.lastIndexOf("/"));
					self.routes.push( {id : routes[key], path :s+"/"});
				}
			}
			
		}, function(error) {
			
		});
	};
	
	
	
	self.getExpiration();
	
	self.getRoutes();
	
	
	var timer = $interval(function()
	{
		var current = new Date();
		var currTime = current.getTime();
		
		var secs = (self.expiration - currTime)/1000
		secs = Math.floor(secs)+1;
		if(secs <= 0)
		{
			self.isExpired = true;
			self.killTimer();
		}
		var mins = 0;
		var hour = 0;
		if(secs>59)
		{
			mins = secs/60;
			secs = secs%60;
			mins = Math.floor(mins);
			if(mins > 59)
			{
				hour = mins/60;
				hour = Math.floor(hour);
			}
		}
		if(secs<10)secs="0"+secs;
		if(mins<10)mins="0"+mins;
		if(hour<10)hour="0"+hour;
		
		
		self.remain = hour+":"+mins+":"+secs;
		
	},1000);
	
	self.killTimer = function()
	{
		$interval.cancel(timer);
		window.location.assign("/");
	};
	
}]);