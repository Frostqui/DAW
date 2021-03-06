package urteam.user;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import urteam.community.Community;
import urteam.event.Event;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
	
	

	public interface MinimalUser extends User.MinimalUser {
	}

	public interface BasicUser extends User.MinimalUser, User.BasicUser {
	}

	public interface CompleteUser
			extends User.MinimalUser, User.CompleteUser, User.BasicUser, Event.MinimalEvent, Community.MinimalCommunity {
	}

	public interface FriendUser extends User.MinimalUser, User.CompleteUser {
	}

	@Autowired
	private UserService userService;

	@Autowired
	private UserComponent userComponent;

	@JsonView(MinimalUser.class)
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<User>> getUsers() {
		List<User> users = userService.getUsers();
		if (users != null) {
			return new ResponseEntity<>(users, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(CompleteUser.class)
	@RequestMapping(value = "/{nickname}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> getUser(@PathVariable String nickname) {
		User user = userService.getUser(nickname);
		if (user != null) {
			return new ResponseEntity<>(user, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(BasicUser.class)
	@RequestMapping(value = "/{nickname}", method = RequestMethod.PUT)
	public ResponseEntity<User> updateUserInfo(@PathVariable String nickname, @RequestBody User user) {
		User updatedUser = userService.getUser(nickname);
		User userLogged = userService.findOne(userComponent.getLoggedUser().getId());
		if (updatedUser == userLogged) {
			if (updatedUser != null) {
				updatedUser = userService.updateUserInfo(nickname, user);
				return new ResponseEntity<>(updatedUser, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		}
	}

	@JsonView(BasicUser.class)
	@RequestMapping(value = "/{nickname}/avatar", method = RequestMethod.PUT)
	public ResponseEntity<User> updateUserAvatar(@PathVariable String nickname, @RequestBody MultipartFile file) {
		User updatedUserAvatar = userService.getUser(nickname);
		if (userService.findOne(userComponent.getLoggedUser().getId()) != null) {
			User userLogged = userService.findOne(userComponent.getLoggedUser().getId());
			if (updatedUserAvatar == userLogged) {
				if (updatedUserAvatar != null) {
					updatedUserAvatar = userService.updateUserAvatar(nickname, file);
					return new ResponseEntity<>(updatedUserAvatar, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@JsonView(BasicUser.class)
	@RequestMapping(value = "/{nickname}/avatar", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getUserAvatar(@PathVariable String nickname) {
		byte[] userAvatar;
		try {
			userAvatar = userService.getUserAvatar(nickname);
			if (userAvatar != null) {
				final HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.IMAGE_JPEG);
				return new ResponseEntity<>(userAvatar, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(BasicUser.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<User> createUser(@RequestBody User user ) {
		User newUser = userService.createNewUser(user, user.getPasswordHash());
		if (newUser != null) {
			return new ResponseEntity<>(newUser, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@JsonView(MinimalUser.class)
	@RequestMapping(value = "/{nickname}/friends", method = RequestMethod.PUT)
	public ResponseEntity<List<User>> followUnfollowUser(@PathVariable String nickname) {
		if (userService.findOne(userComponent.getLoggedUser().getId()) != null) {
			User userToFollow = userService.getUser(nickname);
			User userLogged = userService.findOne(userComponent.getLoggedUser().getId());
			if (userLogged != userToFollow) {
				List<User> friends = userService.getFriends(nickname);
				if (friends != null) {
					friends = userService.followUnfollow(userLogged, nickname);
					return new ResponseEntity<>(friends, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@JsonView(FriendUser.class)
	@RequestMapping(value = "/{nickname}/friends", method = RequestMethod.GET)
	public ResponseEntity<List<User>> getFriends(@PathVariable String nickname) {

		List<User> friends = userService.getFriends(nickname);
		if (friends != null) {
			return new ResponseEntity<>(friends, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(FriendUser.class)
	@RequestMapping(value = "/{nickname}/followers", method = RequestMethod.GET)
	public ResponseEntity<List<User>> getFollowers(@PathVariable String nickname) {
		List<User> followers = userService.getFollowers(nickname);
		if (followers != null) {
			return new ResponseEntity<>(followers, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
