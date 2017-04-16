/**
 *  Login and SignIn Controller
 */

bootLogin.controller('loginCtrl',['$http','$scope',function($http,$scope)
{
	var self = $scope;
	
	self.user = {
			name : '',
			pwd : ''
	};
	
	self.repeatPwd = '';
	
	self.createUser=false;
	self.isCreated=false;
	
	self.signIn = function()
	{
		$http.post("/signIn",self.user).then(function(data) {
			self.createUser=false;
			self.isCreated=true;
		}, function(error) {
			
		});
	};
	

	if(document.cookie.indexOf("ShoeCookie") !== -1)
	{
		document.getElementById("frmLogin").submit();
	}
	
}]);
