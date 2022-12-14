package com.cg.repository;

import com.cg.model.User;
import com.cg.model.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getByUsername(String username);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByPhone(String phone);

    @Query("SELECT NEW com.cg.model.dto.UserDTO (u.id, u.username) FROM User u WHERE u.username = ?1")
    Optional<UserDTO> findUserDTOByUsername(String username);

    /*Hiển thị list danh sách ra*/
    @Query("SELECT NEW com.cg.model.dto.UserDTO(u.id, u.username, u.password , u.fullName , u.phone , u.urlImage, u.createdAt, u.updatedAt, u.locationRegion,  u.role ) FROM User AS u WHERE u.deleted = false")
    List<UserDTO> findAllUserDTOByDeletedIsFalse();



    @Query("SELECT NEW com.cg.model.dto.UserDTO(u.id, u.username , u.fullName , u.phone, u.urlImage, u.locationRegion , u.role) FROM User AS u " +
            "JOIN LocationRegion AS loca ON loca.id = u.locationRegion.id " +
            "JOIN Role AS r ON r.id = u.role.id " +
            "WHERE u.deleted = false AND CONCAT(u.id, u.username, u.fullName , u.phone, u.urlImage, loca.provinceId ,loca.provinceName , r.code) LIKE %?1% ")
    List<UserDTO> search(String keywork);


//    @Query("SELECT NEW com.cg.model.dto.UserDTO(u.id, u.username , u.fullname , u.phone) FROM User AS u  WHERE CONCAT(u.id, u.username , u.fullname , u.phone) LIKE %?1% ")
//    List<UserDTO> search(String keywork);


    Boolean existsByUsernameAndIdIsNot(String username, Long id);

    Boolean existsByPhoneAndIdIsNot(String phone, Long id);
}
