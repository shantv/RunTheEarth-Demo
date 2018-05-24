package run;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.annotation.JsonView;

import run.config.View;
import run.model.ActivityType;
import run.model.Friend;
import run.model.GeoLocation;
import run.model.MmrResponse;
import run.model.UserDetail;
import run.model.Workout;
import run.model.WorkoutRoute;
import run.repo.ActivityRepository;
import run.repo.UserDetailRepository;
import run.repo.WorkoutRepository;
import run.repo.WorkoutRouteRepository;
import run.services.MapMyRunService;

@Controller
public class MapMyRunController {
    private static Log logger = LogFactory.getLog(MapMyRunController.class);
    @Autowired MapMyRunService runService;
    @Autowired UserDetailRepository userRepo;
    @Autowired WorkoutRouteRepository workoutRouteRepo;
    @Autowired WorkoutRepository workoutRepo;
    @Autowired ActivityRepository actRepo;
    @Autowired HttpSession session;
    @Autowired @Qualifier("restOperations") RestOperations restOperations;

    @RequestMapping("/mapmyrun/updateActivityTypes")
	public @ResponseBody Object updateActivityTypes() {
		return runService.updateActivityTypes();
	}
    @RequestMapping("/mapmyrun/activitys")
	public @ResponseBody List<ActivityType> getActivitys() {
    	logger.info("Get Activitys");
		return actRepo.findAll();
	}
    @RequestMapping("/mapmyrun/activity")
	public @ResponseBody ActivityType getActivityType(@RequestParam String activityId) {
		ActivityType act = actRepo.findByActivityId(activityId);
    	logger.info("Get Activity: " + activityId + " " + act);
    	return act;
	}
    @RequestMapping("/mapmyrun/location")
	public @ResponseBody GeoLocation getLocation(@RequestParam double lat, @RequestParam double lng) throws Exception {
    	return runService.getLocation(lat, lng);
	}

    @JsonView(View.Summary.class)
    @RequestMapping("/mapmyrun/routes")
	public @ResponseBody MmrResponse<WorkoutRoute> routes(@RequestParam(required=false) String userId, @RequestParam(required=false,defaultValue="0") int offset, @RequestParam(required=false,defaultValue="20") int limit) {
    	if (userId == null) {
    		userId = ((UserDetail)session.getAttribute("user")).getUserId();
    	}
    	return runService.getRoutes(userId, offset, limit);
	}
    @RequestMapping("/mapmyrun/updateFriends")
	public @ResponseBody List<Friend> updateFriends() {
    	logger.info("Updating friends");
		return runService.updateFriends().getData().get("friendships");
	}
    @RequestMapping("/mapmyrun/friends")
	public @ResponseBody List<UserDetail> friends() {
		return runService.getFriends();
	}
    @JsonView(View.Summary.class)
    @RequestMapping("/mapmyrun/workouts")
	public @ResponseBody MmrResponse<Workout> workouts(@RequestParam(required=false) String userId, @RequestParam(required=false,defaultValue="0") int offset, @RequestParam(required=false,defaultValue="20") int limit) {
    	if (userId == null) {
    		userId = ((UserDetail)session.getAttribute("user")).getUserId();
    	}
    	return runService.getWorkouts(userId, offset, limit);
	}
    @RequestMapping("/mapmyrun/workoutsAll")
	public @ResponseBody List<Workout> workoutsAll(@RequestParam String userId) {
    	return workoutRepo.findByUserId(userId);
	}

    @JsonView(View.Summary.class)
    @RequestMapping("/mapmyrun/updateWorkouts")
	public @ResponseBody MmrResponse<Workout> updateWorkouts(@RequestParam int offset, @RequestParam int limit, @RequestParam(required=false) String userId) {
    	logger.info("Updating workouts");
    	UserDetail userDetail = (UserDetail) session.getAttribute("user");
    	if (userId == null) userId = userDetail.getUserId();
		return runService.updateWorkouts(offset, limit, userId);
	}
    @JsonView(View.Summary.class)
    @RequestMapping("/mapmyrun/updateRoutes")
	public @ResponseBody MmrResponse<WorkoutRoute> updateRoutes(@RequestParam int offset, @RequestParam int limit, @RequestParam(required=false) String userId) {
		return runService.updateRoutes(offset, limit, userId);
	}
    @RequestMapping("/mapmyrun/user")
	public @ResponseBody UserDetail user(@RequestParam(required=false) String userId) {
    	logger.info("GET USER DETAIL: " + userId);
		return runService.getUserDetails(userId);
	}

	@RequestMapping({ "/user", "/me" })
	public @ResponseBody Map<String, Object> user(HttpServletRequest req, HttpSession session, Principal principal) throws Exception {
		logger.info("GET USER/ME PAGE: " + req.getRemoteUser() + " " + req.getRemoteAddr() + " " + req.getRemoteHost());
		Map<String, Object> map = new LinkedHashMap<>();

		if (principal != null) {
			logger.info("Principal: " + principal.getName());
			String token = principal.getName();
			map.put("token", token);
		}
		Boolean isNew = (Boolean) session.getAttribute("isNew");
		if (isNew != null && isNew) {
			map.put("isNew", true);
			session.setAttribute("isNew", false);			
		}
		map.put("user", req.getSession().getAttribute("user"));
		return map;
	}

	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (Authentication auth, HttpServletRequest request, HttpServletResponse response) {
	    if (auth != null){    
	    	logger.info("Clearing auth");
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
	    return "redirect:/";
	}
	@RequestMapping("/callback")
	public @ResponseBody Map<String, String> callback(HttpSession session, HttpServletResponse response, HttpServletRequest request, @RequestHeader(required=false) HttpHeaders headers) throws IOException, ServletException, URISyntaxException {
		Map<String, String> map = new LinkedHashMap<>();
		List<String> auth = headers.get(HttpHeaders.AUTHORIZATION);
		if (auth != null && auth.size() > 0) {
			map.put("name", auth.get(0));
			request.getSession().setAttribute("token", auth.get(0));
		}
		logger.info("Callback to: " + map);
		return map;
	}
}
