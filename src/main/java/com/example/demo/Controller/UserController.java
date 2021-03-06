package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Exceptions.IllegalIputException;
import com.example.demo.Exceptions.NoUserFound;
import com.example.demo.Model.User;
import com.example.demo.Service.UserService;

/**
 * 
 * @author stipe
 *
 */
@RestController
@RequestMapping("/api")
public class UserController {
	@Autowired
	UserService service;

	/**
	 * 
	 * @param id
	 * @return User
	 * @throws NoUserFound
	 */
	@GetMapping("/user/{id}")
	public User getUser(@PathVariable int id) throws NoUserFound {
		if (service.getUserByid(id).isEmpty())
			throw new NoUserFound("There is no user with "+ id+" id");
		return service.getUserByid(id).get();
	}


	/**
	 * 
	 * @return all users
	 */
	@GetMapping("/allUsers")
	public List<User> getAllUsers(){
		return service.getAllUsers();
	}

	/**
	 * adding new user
	 * @param user
	 * @return added user
	 */
	@PostMapping("/new")
	public User newuser(@RequestBody User user) {
		return service.makeNewUser(user);
	}
	
	/**
	 * deleting user by id
	 * @param id
	 */
	@DeleteMapping("/delete/{id}")
	public void deleteUser(@PathVariable int id) {
		User user= service.getUserByid(id).orElseThrow();
		service.delete(user);
	}//returning respone body
		
	/**
	 * updating user
	 * @param id
	 * @param newUser
	 * @return
	 */
	@PutMapping("/update/{id}")
	public User updateUser(@PathVariable int id,@RequestBody User newUser) {
		User updated= service.getUserByid(id).orElseThrow();
		return service.update(updated, newUser);
		
	}
	
	/**
	 * post methond with key
	 * @param authorization
	 * @return
	 */
	@PostMapping("/safe")
	public String messageByInterceptor(@RequestHeader("key") String authorization) {
		if(authorization.endsWith("0")) throw new IllegalIputException("Wrong key");
		return "inserted "+ authorization +" is valid";
	}


	@Autowired
	private RestTemplate restTemplate;
	/**
	 * exception with interceptor on
	 * runs only if h2 is true
	 * @return
	 */
	@GetMapping("/condition")
	public String conditon() {
		return restTemplate.getForObject("http://localhost:8080/property", String.class);
	}
	
	
	//other ways of doing 
	
	@PostMapping("/newuser")
	public User newUser(@RequestParam("id") int id, @RequestParam("name") String name,
			@RequestParam("job") String job) {
		return service.makeNewUser(id, name, job);
	}

	@PostMapping("/newBodyUser")
	public User newBodyUser(User user) {
		return service.makeNewUser(user);
	}
	@Value("${spring.datasource.url}")
	private String value;

	@GetMapping("/property")
	public String propertiMessage() {
		return value;
	}
}
