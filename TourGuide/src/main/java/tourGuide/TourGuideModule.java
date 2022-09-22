package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rewardCentral.RewardCentral;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {
	
//	@Bean
//	public GpsUtil getGpsUtil() {
//		return new GpsUtil();
//	}
	
	@Bean
	public GpsUtilService getGpsUtilService() {
		return new GpsUtilService();
	}
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getRewardCentral(), getGpsUtilService());
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
}
