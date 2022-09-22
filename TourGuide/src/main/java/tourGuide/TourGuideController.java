package tourGuide;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.VisitedLocation;
import tourGuide.service.UserService;
import tourGuide.user.User;

@RestController
public class TourGuideController {

	@Autowired
	UserService userService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = userService.getUserLocation(getUser(userName));
    	//userService.trackUserLocation(getUser(userName));
		return visitedLocation.location.latitude + ":" + visitedLocation.location.longitude;
    }
    
    private User getUser(String userName) {
    	return userService.getUser(userName);
    }
   
  

}