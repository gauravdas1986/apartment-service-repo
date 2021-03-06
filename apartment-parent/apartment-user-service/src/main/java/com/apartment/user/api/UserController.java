package com.apartment.user.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.apartment.user.exception.UserCustomException;
import com.apartment.user.model.User;
import com.apartment.user.model.UserServiceResponse;
import com.apartment.user.model.ValidateUserResponseBean;
import com.apartment.user.service.UserService;
import com.apartment.user.util.UserConstants;
import com.apartment.util.AppConstants;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService service;

	/**
	 * This method is to save User details
	 * 
	 * @param userDetails
	 * @return
	 * @throws UserCustomException
	 */
	@ApiOperation(value = "Save User details")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Success", response = UserServiceResponse.class),
		@ApiResponse(code = 400, message = "Validation Failure", response = UserServiceResponse.class),
		@ApiResponse(code = 404, message = "Not Found", response = UserServiceResponse.class),
		@ApiResponse(code = 500, message = "Internal Server Error", response = UserServiceResponse.class) 
	})
	@PostMapping(UserConstants.ENDPOINT_CREATE)
	public UserServiceResponse createUser(@Valid @RequestBody User userDetails) throws Exception {
		try (User user = service.createUser(userDetails)) {
			logger.info(AppConstants.SUCCESS);
			return new UserServiceResponse(
					AppConstants.STATUS_CODE,
					AppConstants.MESSAGE,
					new ArrayList<User>(Arrays.asList(user)));
		} catch (Exception exception) {
			logger.error(AppConstants.ERROR_CODE);
			throw new UserCustomException("Record is not found");
		}
	}

	/**
	 * End-point to get all details of userService
	 * @return UserModelBean list
	 */
	@ApiOperation(value = "Get all user details")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Success", response = User.class),
		@ApiResponse(code = 400, message = "Validation Failure", response = User.class),
		@ApiResponse(code = 404, message = "Not Found", response = User.class),
		@ApiResponse(code = 500, message = "Internal Server Error", response = User.class) 
	})
	@GetMapping(UserConstants.ENDPOINT_GETUSERS)
	public List<User> getAllUsers() {
		Optional<List<User>> users = service.getAllUsers();
		if (users.isPresent()) {
			logger.info(AppConstants.SUCCESS);
			return users.get();
		}
		logger.error(AppConstants.ERROR_CODE);
		throw new UserCustomException("No user found!");
	}

	/**
	 * End-point to get user by ID
	 * 
	 * @param userId
	 * @return UserModelBean ResponseEntity
	 * @throws UserCustomException
	 */
	@ApiOperation(value = "Get user details by user-name")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Success", response = User.class),
		@ApiResponse(code = 400, message = "Validation Failure", response = User.class),
		@ApiResponse(code = 404, message = "Not Found", response = User.class),
		@ApiResponse(code = 500, message = "Internal Server Error", response = User.class) 
	})
	@GetMapping(UserConstants.ENDPOINT_GETDETAILS_BY_USERNAME)
	public ResponseEntity<User> getUserByUserName(@PathVariable("userName") String userName)
			throws UserCustomException {
		Optional<User> optionalUser = service.findByUserName(userName);
		if (optionalUser.isPresent())  return new ResponseEntity<User>(optionalUser.get(), HttpStatus.OK);
		throw new UserCustomException(AppConstants.ERROR_CODE);
	}

	/**
	 * End-Point to delete user by userId
	 * 
	 * @param String
	 *            userId
	 * @return String
	 */
	@ApiOperation(value = "Delete user details by user-name")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Success", response = String.class),
		@ApiResponse(code = 400, message = "Validation Failure", response = String.class),
		@ApiResponse(code = 404, message = "Not Found", response = String.class),
		@ApiResponse(code = 500, message = "Internal Server Error", response = String.class) 
	})
	@PutMapping(UserConstants.ENDPOINT_DELETE_BY_USERNAME)
	public String delete(@PathVariable("userName") String userName) {
		service.deleteUserByUserName(userName);
		logger.info(AppConstants.SUCCESS);
		return "The User" + userName + " has been deleted.";
	}

	/**
	 * @param userBean
	 * @param userId
	 */
	@ApiOperation(value = "Update user details")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Success", response = User.class),
		@ApiResponse(code = 400, message = "Validation Failure", response = User.class),
		@ApiResponse(code = 404, message = "Not Found", response = User.class),
		@ApiResponse(code = 500, message = "Internal Server Error", response = User.class) 
	})
	@PutMapping(UserConstants.ENDPOINT_UPDATE)
	public String updateUser(@RequestBody User userBean) {
		service.updateUser(userBean);
		logger.info(AppConstants.SUCCESS);
		return "The User" + userBean.getUserKey().getUserName() + " has been upadted.";
	}

	/**
	 * @param userName
	 * @param password
	 * @return
	 */
	@ApiOperation(value = "Validate user details")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Success", response = ValidateUserResponseBean.class),
		@ApiResponse(code = 400, message = "Validation Failure", response = ValidateUserResponseBean.class),
		@ApiResponse(code = 404, message = "Not Found", response = ValidateUserResponseBean.class),
		@ApiResponse(code = 500, message = "Internal Server Error", response = ValidateUserResponseBean.class) 
	})
	@PostMapping(UserConstants.ENDPOINT_VALIDATION)
	public ResponseEntity<ValidateUserResponseBean> validateUser(@PathVariable("userName") String userName,
			@PathVariable("password") String password) {
		Optional<User> optionalUser = service.validateUser(userName, password);
		if (optionalUser.isPresent())
			return new ResponseEntity<ValidateUserResponseBean>(
					new ValidateUserResponseBean(optionalUser.get(), true, optionalUser.get().getUserRole()),
					HttpStatus.OK);
		throw new UserCustomException(AppConstants.ERROR_CODE);
	}

}
