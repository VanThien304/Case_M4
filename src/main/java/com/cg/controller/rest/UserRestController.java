package com.cg.controller.rest;

import com.cg.exception.DataInputException;
import com.cg.exception.EmailExistsException;
import com.cg.exception.ResourceNotFoundException;
import com.cg.model.Product;
import com.cg.model.Role;
import com.cg.model.User;
import com.cg.model.dto.UserDTO;
import com.cg.service.role.IRoleService;
import com.cg.service.user.IUserService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {



    @Autowired
    private IUserService userService;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private IRoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAllUserDTO(){

        List<UserDTO> userDTO = userService.findAllUserDTOByDeletedIsFalse();

        return new ResponseEntity<>(userDTO , HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> doCreate(@Validated @RequestBody UserDTO userDTO, BindingResult bindingResult){
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        userDTO.setId(0L);
        userDTO.getLocationRegion().setId(0L);

        userDTO.setUrlImage("user.png");

        Boolean existsById  = userService.existsById(userDTO.getId());
        if (existsById) {
            throw new EmailExistsException("ID đã tồn tại vui lòng nhập lại!");
        }


        Boolean existsByUsername  = userService.existsByUsername(userDTO.getUsername());
        if (existsByUsername) {
            throw new EmailExistsException("Username đã tồn tại vui lòng nhập lại!");
        }

        Boolean existsByPhone = userService.existsByPhone(userDTO.getPhone());
        if (existsByPhone) {
            throw new EmailExistsException("Phone đã tồn tại");
        }

        Optional<Role> role = roleService.findById(userDTO.getRole().getId());

        if(!role.isPresent()){
            throw new EmailExistsException("ID ROLE không tồn tại!");
        }

        try{
            User user = userDTO.toUser();
            User newUser = userService.saveUpdate(user);

            return new ResponseEntity<>(newUser.toUserDTO(), HttpStatus.CREATED);

        }catch (DataIntegrityViolationException e){
            throw new DataInputException("Thông tin tài khoản không hợp lệ ");
        }

    }


    @PutMapping("/update")
    public ResponseEntity<?> doUpdate(@Validated @RequestBody UserDTO userDTO, BindingResult bindingResult){

        if(bindingResult.hasFieldErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        Optional<User> userDTOOptional = userService.findById(userDTO.getId());
        if(!userDTOOptional.isPresent()){
            throw new DataInputException("Không tìm thấy ID người dùng!");


        }
        Boolean exitsByUserName = userService.existsByUsernameAndIdIsNot(userDTO.getUsername(), userDTO.getId());
        if(exitsByUserName){
            throw new EmailExistsException("UserName đã tồn tại!");
        }

        Boolean existsByPhone = userService.existsByPhoneAndIdIsNot(userDTO.getPhone(), userDTO.getId());
        if (existsByPhone) {
            throw new EmailExistsException("Phone đã tồn tại");
        }

        Optional<Role> roleId = roleService.findById(userDTO.getRole().getId());
        if(!roleId.isPresent()){
            throw new EmailExistsException("ID ROLE không tồn tại!");
        }
        userDTO.getLocationRegion().setId(0L);

        try {
            userDTOOptional.get().setUsername(userDTO.getUsername());
            userDTOOptional.get().setFullName(userDTO.getFullName());
            userDTOOptional.get().setPassword("123");
            userDTOOptional.get().setPhone(userDTO.getPhone());
            userDTOOptional.get().setRole(userDTO.getRole().toRole());
            userDTOOptional.get().setLocationRegion(userDTO.getLocationRegion().toLocationRegion());
            userDTOOptional.get().setUrlImage("user.png");
            User updateUser = userService.saveUpdate(userDTOOptional.get());

            return new ResponseEntity<> (updateUser.toUserDTO(), HttpStatus.ACCEPTED);

        }catch (DataIntegrityViolationException e){
            throw new DataInputException("Thông tin tài khoản không hợp lệ");
        }
    }

    //Hàm hiển thị dữ liệu Edit theo id, tìm theo id để đổ dữ liệu về
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id) {

        Optional<User> userOptional = userService.findById(id);

        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("Invalid customer ID");
        }

        return new ResponseEntity<>(userOptional.get().toUserDTO(),  HttpStatus.OK);
    }

    @GetMapping("/search/{keySearch}")
    public ResponseEntity<?> getSearchUserDTO(@PathVariable String keySearch){

        List<UserDTO> userDTO = userService.searchAllUser(keySearch);

        if(userDTO.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(userDTO , HttpStatus.OK);
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            user.setDeleted(true);
            userService.save(user);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
