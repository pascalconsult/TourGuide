package fixed.tourGuide;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tourGuide.user.User;


public class TestUserService {
	
	@Test
	public void getUserLocation() {
		GpsUtilService gpsUtilService = new GpsUtilService(); 
		RewardsService rewardsService = new RewardsService(new RewardCentral(), gpsUtilService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserService userService = new UserService(gpsUtilService, rewardsService);
		userService.trackUserLocation(user);
		while(user.getVisitedLocations().isEmpty()) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		userService.tracker.stopTracker();

		assertTrue(user.getVisitedLocations().get(0).userId.equals(user.getUserId()));
	}
	
	@Test
	public void highVolumeTrackLocation() throws InterruptedException, ExecutionException {
		GpsUtilService gpsUtilService = new GpsUtilService(); 
		RewardsService rewardsService = new RewardsService(new RewardCentral(), gpsUtilService);
		InternalTestHelper.setInternalUserNumber(1000);
		UserService userService = new UserService(gpsUtilService, rewardsService);
		List<User> allUsers = userService.getAllUsers();
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(User user : allUsers) {
			userService.trackUserLocation(user);
		}
		
		for(User user : allUsers) {
			while(user.getVisitedLocations().size() < 4) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
		
		for(User user: allUsers) {
			VisitedLocation visitedLocation = user.getVisitedLocations().get(3);
			assertTrue(visitedLocation != null);
		}
		userService.tracker.stopTracker();

		stopWatch.stop();
		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
}
