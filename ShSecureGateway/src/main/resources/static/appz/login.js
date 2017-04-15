/**
 * 
 */

bootLogin.controller('loginCtrl',['$http','$scope',function($http,$scope)
{
	var self = $scope;
	
	self.user = {
			name : '',
			pwd : ''
	};
	
	self.repeatPwd = '';
	
	self.signIn = function()
	{
		$http.post("/signIn",self.user).then(function(data) {
			
		}, function(error) {
			
		});
	};
	
	if(document.cookie.indexOf("ShoeCookie") !== -1)
	{
		document.getElementById("frmLogin").submit();
	}
	
}]);