package fixed.tourGuide;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public class TestRewardsService {
	

	@Test
	public void usersGetRewards() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		GpsUtilService gpsUtilService = new GpsUtilService(); 
		RewardsService rewardsService = new RewardsService(new RewardCentral(), gpsUtilService);
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtilService, rewardsService);
		userService.addUser(user);
		assertTrue(user.getUserRewards().size() == 0);
		
		Attraction attraction = gpsUtilService.getAttractions().get(0);
		user.clearVisitedLocations();
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		rewardsService.calculateRewards(user);
		
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}

		userService.tracker.stopTracker();
		List<UserReward> userRewards = user.getUserRewards();
		
		assertTrue(userRewards.size() > 0);
	}

	@Test
	public void highVolumeGetRewards() {
		GpsUtilService gpsUtilService = new GpsUtilService(); 
		RewardsService rewardsService = new RewardsService(new RewardCentral(), gpsUtilService);
		Attraction attraction = gpsUtilService.getAttractions().get(0);
		InternalTestHelper.setInternalUserNumber(100000);
		UserService userService = new UserService(gpsUtilService, rewardsService);
		List<User> allUsers = userService.getAllUsers();
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(User user : allUsers) {
			user.clearVisitedLocations();
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		}
		
		for(User user : allUsers) {
			while(user.getUserRewards().isEmpty()) {
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
				}
			}
		}
		userService.tracker.stopTracker();

		stopWatch.stop();
		System.out.println("Seconds ellapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
		
		for(User user: allUsers) {
			assertTrue(userService.getUserRewards(user).size() >= 1);
		}
		assertTrue(TimeUnit.MINUTES.toSeconds(30) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
