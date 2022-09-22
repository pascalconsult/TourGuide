package tourGuide.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gpsUtil.location.VisitedLocation;

public class UserHistory {
	
//	private final Map<UUID, List<VisitedLocation>> userHistoryMap;
//	
//	public UserHistory() {
//		userHistoryMap = new HashMap<>();
//	}
//	
//	public void storeUserLocation(VisitedLocation visitedLocation) {
//		if(userHistoryMap.containsKey(visitedLocation.userId)) {
//			userHistoryMap.get(visitedLocation.userId).add(visitedLocation);
//		} else {
//			userHistoryMap.put(visitedLocation.userId, new ArrayList<>());
//			userHistoryMap.get(visitedLocation.userId).add(visitedLocation);
//		}
//	}
//	
//	public List<VisitedLocation> getUserHistory(UUID userId) {
//		List<VisitedLocation> visitedLocations = userHistoryMap.get(userId);
//		return (visitedLocations == null) ? new ArrayList<>() : visitedLocations;
//	}

}
