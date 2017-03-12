package urteam.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import urteam.ConstantsUrTeam;
import urteam.urteamController;
import urteam.community.Community;
import urteam.sport.SportController;
import urteam.sport.SportRepository;

@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SportController sportController;

	@Autowired
	private urteamController urteamController;
	

	@RequestMapping("/newUser")
	public String eventAdded(Model model, User user) throws ParseException {
		Date date = new Date();
		SimpleDateFormat userIdFormater = new SimpleDateFormat("mmddyyyy-hhMMss");
		String generatedId = userIdFormater.format(date);
		user.setGeneratedId(generatedId);
		userRepository.save(user);
		return "redirect:/events";
	}

	@RequestMapping("/userprofile/{id}")
	public String userProfile(Model model, @PathVariable Long id) {
		User user = userRepository.findOne(id);
		model.addAttribute("user", user);
		List<User> friends = user.getFollowing();
		List<Community> communities = user.getCommunityList();
		model.addAttribute("following", friends);
		model.addAttribute("communities", communities);
		model.addAttribute("members", communities.size());
		model.addAttribute("numberOfFollowers", user.getNumberOfFollower());
		model.addAttribute("sportList",sportController.getSportList());
		return "user";
	}

	@RequestMapping("/userprofile/{id}/edit")
	public String userProfileEdit(Model model, @PathVariable long id, @RequestParam String username,
			@RequestParam String surname, @RequestParam String email, @RequestParam String bio,
			@RequestParam String city, @RequestParam String country, @RequestParam("file") MultipartFile file)
			throws ParseException {
		User editedUser = userRepository.findOne(id);

		editedUser.setUserName(username);
		editedUser.setSurname(surname);
		editedUser.setEmail(email);
		editedUser.setBio(bio);
		editedUser.setCity(city);
		editedUser.setCountry(country);

		if (file != null) {
			String filename = "avatar-" + editedUser.getGeneratedId();

			if (urteamController.uploadImageFile(model, file, filename, ConstantsUrTeam.USER_AVATAR,
					editedUser.getGeneratedId())) {
				editedUser.setAvatar(filename);
			}
		}
		userRepository.save(editedUser);

		return "redirect:/userprofile/{id}";
	}

	@RequestMapping("user/{id}/follow/{toFollowUser}")
	public String followSomeUser(Model model, @PathVariable long id, @PathVariable long toFollowUser) {

		return "na";
	}

	@RequestMapping("user/{id}/unfollow/{toFollowUser}")
	public String unfollowSomeUser(Model model, @PathVariable long id, @PathVariable long toFollowUser) {

		return "na";
	}
}
