package run.services;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import run.model.ActivityType;
import run.model.Friend;
import run.model.GeoLocation;
import run.model.MmrResponse;
import run.model.UserDetail;
import run.model.Workout;
import run.model.WorkoutRoute;
import run.repo.ActivityRepository;
import run.repo.FriendRepository;
import run.repo.UserDetailRepository;
import run.repo.WorkoutRepository;
import run.repo.WorkoutRouteRepository;

@Component
public class MapMyRunService {
    private static Log logger = LogFactory.getLog(MapMyRunService.class);
	@Autowired HttpSession session;
    @Autowired @Qualifier("restOperations") RestOperations restOperations;
    @Value("${mapmyrun.client.clientId}")
    private String apiKey;
    @Autowired WorkoutRepository workoutRepo;
    @Autowired WorkoutRouteRepository workoutRouteRepo;
    @Autowired UserDetailRepository userRepo;
    @Autowired ActivityRepository actRepo;
    @Autowired FriendRepository friendRepo;
    
	public MapMyRunService() { }

	public HttpEntity requestEntity() {
		String token = (String) session.getAttribute("token");
		HttpHeaders requestHeaders = new HttpHeaders();  
	    requestHeaders.set(HttpHeaders.ACCEPT_ENCODING, "GZIP");
	    requestHeaders.set("Api-Key", apiKey);
	    requestHeaders.set("Authorization", token);

	    HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
	    return requestEntity;
	}

	public UserDetail getUserDetails() {
		return getUserDetails("self");
	}
	public UserDetail getUserDetails(String userId) {
		String token = (String) session.getAttribute("token");
		UserDetail user = null;
		try {
			HttpHeaders requestHeaders = new HttpHeaders();  
		    requestHeaders.set(HttpHeaders.ACCEPT_ENCODING, "GZIP");
		    requestHeaders.set("Api-Key", apiKey);
		    requestHeaders.set("Authorization", token);

		    HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
			ResponseEntity<UserDetail> responseEntity = restOperations.exchange("https://oauth2-api.mapmyapi.com/v7.1/user/"+userId, HttpMethod.GET, requestEntity, UserDetail.class);
			user = responseEntity.getBody();
			if (userId.equals("self")) 
				user.setAuthToken(token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	public Object updateActivityTypes() {
		MmrResponse<ActivityType> w = null;
		try {
			logger.info("Updating activity types");
			String token = (String) session.getAttribute("token");
			RequestEntity request = RequestEntity.get(new URI("https://oauth2-api.mapmyapi.com/v7.1/activity_type")).accept(MediaType.APPLICATION_JSON)
					.header("Api-Key", apiKey)
					.header("Authorization",token)
					.build();
			ParameterizedTypeReference<MmrResponse<ActivityType>> myBean = new ParameterizedTypeReference<MmrResponse<ActivityType>>() {};
			ResponseEntity<MmrResponse<ActivityType>> result = restOperations.exchange(request, myBean);
			w = result.getBody();
			List<ActivityType> list = w.getData().get("activity_types");
			actRepo.save(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return w;
	}	
	public MmrResponse<Friend> updateFriends() {
		MmrResponse<Friend> w = null;
		try {
			logger.info("Updating friends");
			String token = (String) session.getAttribute("token");
			UserDetail user = (UserDetail) session.getAttribute("user");
			String userId = user.getUserId();
			RequestEntity request = RequestEntity.get(new URI("https://oauth2-api.mapmyapi.com/v7.1/friendship?from_user="+userId)).accept(MediaType.APPLICATION_JSON)
					.header("Api-Key", apiKey)
					.header("Authorization",token)
					.build();
			ParameterizedTypeReference<MmrResponse<Friend>> myBean = new ParameterizedTypeReference<MmrResponse<Friend>>() {};
			ResponseEntity<MmrResponse<Friend>> result = restOperations.exchange(request, myBean);
			w = result.getBody();
			List<Friend> list = w.getData().get("friendships");
			list.forEach(f -> {
				UserDetail friend = getUserDetails(f.getFriendUserId());
				userRepo.save(friend);
			});
			friendRepo.save(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return w;
	}	
	public List<UserDetail> getFriends() {
		UserDetail user = (UserDetail) session.getAttribute("user");
		String userId = user.getUserId();
		List<Friend> friends = friendRepo.findByUserId(userId);
		Set<String> friendIds = friends.stream().map(Friend::getFriendUserId).collect(Collectors.toSet());		
		List<UserDetail> users = userRepo.findAll().stream().filter(p -> friendIds.contains(p.getUserId())).collect(Collectors.toList());
		return users;
	}
	public MmrResponse<Workout> updateWorkouts(int offset, int limit, String userId) {
		try {
			UserDetail user = new UserDetail();
			if (userId == null) {		    
				user = (UserDetail) session.getAttribute("user");
				userId = user.getUserId();				
			}

			ParameterizedTypeReference<MmrResponse<Workout>> myBean = new ParameterizedTypeReference<MmrResponse<Workout>>() {};
			ResponseEntity<MmrResponse<Workout>> routeResponse = restOperations.exchange("https://oauth2-api.mapmyapi.com/v7.1/workout/?offset="+offset+"&limit="+limit+"&user="+userId, HttpMethod.GET, requestEntity(), myBean);
			MmrResponse<Workout> response = routeResponse.getBody();

			List<Workout> workouts = (List<Workout>)response.getData().get("workouts");
			workoutRepo.save(workouts);
			List<Workout> workoutList = workoutRepo.findByUserId(userId);
			if (workoutList.size() < response.getTotalCount()) {
				response.setOffset(offset+limit);
			} else {
				response.setOffset(0);
			}
			logger.info("We have: " + workoutList.size() + " of " + response.getTotalCount() + " setting offset to: " + response.getOffset());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public GeoLocation getLocation(double lat, double lng) {
		GeoLocation l = null;
		try {
			URI uri = new URI("https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+"&key=<***EXCLUDED***>&result_type=administrative_area_level_1");
			ResponseEntity<GeoLocation> location = restOperations.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, GeoLocation.class);
			l = location.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}
	public MmrResponse<WorkoutRoute> updateRoutes(int offset, int limit, String userId) {
		try {
			UserDetail user = new UserDetail();
			if (userId == null) {		    
				user = (UserDetail) session.getAttribute("user");
				userId = user.getUserId();				
			}
			logger.info("Updating routes: " + user.getUsername() + " " + userId + " " + offset);

			ParameterizedTypeReference<MmrResponse<WorkoutRoute>> myBean = new ParameterizedTypeReference<MmrResponse<WorkoutRoute>>() {};
			ResponseEntity<MmrResponse<WorkoutRoute>> routeResponse = restOperations.exchange("https://oauth2-api.mapmyapi.com/v7.1/route/?user="+userId+"&field_set=detailed&offset="+offset+"&limit="+limit+"&order_by=-date_created", HttpMethod.GET, requestEntity(), myBean);
			MmrResponse<WorkoutRoute> response = routeResponse.getBody();
			List<WorkoutRoute> routes = (List<WorkoutRoute>) response.getData().get("routes");

			workoutRouteRepo.save(routes);
			List<WorkoutRoute> workouts = workoutRouteRepo.findByUserId(userId);
			if (workouts.size() < response.getTotalCount()) {
				response.setOffset(offset+limit);
			} else {
				response.setOffset(0);
			}
			logger.info("We have: " + workouts.size() + " of " + response.getTotalCount() + " setting offset to: " + response.getOffset());
			return response;
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		return null;
	}	
	public WorkoutRoute getRoute(String routeId) {
		String token = (String) session.getAttribute("token");
		try {
			UserDetail user = (UserDetail) session.getAttribute("user");
			RequestEntity request = RequestEntity.get(new URI("https://oauth2-api.mapmyapi.com/v7.1/route/"+routeId+"?field_set=detailed&format=json")).accept(MediaType.APPLICATION_JSON)
					.header("Api-Key", apiKey)
					.header("Authorization",token)
					.build();
			ParameterizedTypeReference<WorkoutRoute> myBean = new ParameterizedTypeReference<WorkoutRoute>() {};
			ResponseEntity<WorkoutRoute> result = restOperations.exchange(request, myBean);
			WorkoutRoute route = result.getBody();
			return route;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public MmrResponse<WorkoutRoute> getRoutes(String userId, int offset, int limit) {
		UserDetail user = userRepo.findByUserId(userId);
		MmrResponse<WorkoutRoute> r = new MmrResponse<WorkoutRoute>();

		if (user == null) return r;
    	
		List<WorkoutRoute> routes = workoutRouteRepo.findByUserId(user.getUserId(), new PageRequest(offset, limit));
    	int totalCount = workoutRouteRepo.countByUserId(userId);
		Map<String,List<WorkoutRoute>> map = new HashMap<String,List<WorkoutRoute>>();
		map.put("routes", routes);
		r.setTotalCount(totalCount);
		r.setData(map);
		r.setOffset(offset+1);
		if (routes.size() == 0) r.setOffset(0);
		if ((offset * limit)> totalCount)
			r.setOffset(0);
    	logger.info("Routes: " + user.getUsername() + " " + user.getUserId()+ " Offset: " + (offset*limit) + " " + routes.size());
		routes = null;
		
    	return r;
	}
	public MmrResponse<Workout> getWorkouts(String userId, int offset, int limit) {
		UserDetail user = userRepo.findByUserId(userId);
		MmrResponse<Workout> r = new MmrResponse<Workout>();

		if (user == null) return r;
    	
		List<Workout> workouts = workoutRepo.findByUserId(user.getUserId(), new PageRequest(offset, limit));
    	int totalCount = workoutRepo.countByUserId(userId);
		Map<String,List<Workout>> map = new HashMap<String,List<Workout>>();
		map.put("workouts", workouts);
		r.setTotalCount(totalCount);
		r.setData(map);
		r.setOffset(offset+1);
		if (workouts.size() == 0) r.setOffset(0);
		if ((offset * limit)> totalCount)
			r.setOffset(0);
    	logger.info("Workouts: " + user.getUsername() + " " + user.getUserId()+ " Offset: " + (offset*limit) + " " + workouts.size());
		workouts = null;
		
    	return r;
	}

}
