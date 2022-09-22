package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class UserService {
	private Logger logger = LoggerFactory.getLogger(UserService.class);

	private final GpsUtilService gpsUtilService;
	private final RewardsService rewardsService;
	
	public final Tracker tracker;
	ExecutorService executor = Executors.newFixedThreadPool(1000);
	
	public UserService(GpsUtilService gpsUtilService, RewardsService rewardsService) {
		this.gpsUtilService = gpsUtilService;
		this.rewardsService = rewardsService;
		
		logger.debug("Initializing users");
		initializeInternalUsers();
		logger.debug("Finished initializing users");
		tracker = new Tracker(this);
		initializeTripPricer();
		addShutDownHook();
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        System.out.println("Shutdown UserService");
		        tracker.stopTracker();
		      } 
		    }); 
	}
	
	private void initializeTripPricer() {
		logger.debug("Initialize tripPricer");
	}
	
	public VisitedLocation getUserLocation(User user) {
		return user.getVisitedLocations().get(0);
	}
	
	public void trackUserLocation(User user) {
		gpsUtilService.submitLocation(user, this);
	}
	
	public void finalizeLocation(User user, VisitedLocation visitedLocation) {
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		tracker.finalizeTrack(user);
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public void addUser(User user) {
		internalUserMap.put(user.getUserName(), user);
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new ConcurrentHashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

//	private void initializeInternalUsers() {
//		IntStream.range(0, 100000).forEach(i -> {
//			String userName = "internalUser"+i;
//			String phone = "000";
//			String email = userName+"@tourGuide.com";
//			UUID userId = UUID.randomUUID();
//			User user = new User(UUID.randomUUID(), userName, phone, email);
//			generateUserLocationHistory(user);
//			
//			internalUserMap.put(userName, user);
//		});	
//	}
//	
//	private void generateUserLocationHistory(User user) {
//		IntStream.range(0, 2).forEach(i-> {
//			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
//		});
//	}
//	
//	private double generateRandomLongitude() {
//		double leftLimit = -180;
//	    double rightLimit = 180;
//	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
//	}
//	
//	private double generateRandomLatitude() {
//		double leftLimit = -85.05112878;
//	    double rightLimit = 85.05112878;
//	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
//	}
//	
//	private Date getRandomTime() {
//		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
//	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
//	}
	
}
