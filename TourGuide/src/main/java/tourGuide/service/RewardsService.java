package tourGuide.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class RewardsService {
	private final Logger logger = LoggerFactory.getLogger(RewardsService.class);
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	private int proximityBufferMiles = 10;
	
	private final RewardCentral rewardsCentral;
	
	private final GpsUtilService gpsUtilService;
	
	private ExecutorService executor = Executors.newFixedThreadPool(1000);

	public RewardsService(RewardCentral rewardCentral, GpsUtilService gpsUtilService) {
		this.rewardsCentral = rewardCentral;
		this.gpsUtilService = gpsUtilService;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBufferMiles = proximityBuffer;
	}
	
	public void calculateRewards(User user) {
		List<Attraction> attractions = gpsUtilService.getAttractions();
		List<VisitedLocation> visitedLocationList = user.getVisitedLocations().stream().collect(Collectors.toList());
		for(VisitedLocation visitedLocation : visitedLocationList) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					calculateDistanceReward(user, visitedLocation, attraction);
				}
			}
		}
	}
	
	public void calculateDistanceReward(User user, VisitedLocation visitedLocation, Attraction attraction) {
		Double distance = getDistance(attraction, visitedLocation.location);
		if(distance <= proximityBufferMiles) {
			UserReward userReward = new UserReward(visitedLocation, attraction, distance.intValue());
			submitRewardPoints(userReward, attraction, user);
		}
	}
	
	private void submitRewardPoints(UserReward userReward, Attraction attraction, User user) {
		//userReward.setRewardPoints(10);
		//user.addUserReward(userReward);
		CompletableFuture.supplyAsync(() -> {
		    return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
		}, executor)
			.thenAccept(points -> { 
				userReward.setRewardPoints(points);
				user.addUserReward(userReward);
			});
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}
	
}
