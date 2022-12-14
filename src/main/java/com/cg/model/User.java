package com.cg.model;

import com.cg.model.dto.UserDTO;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Accessors(chain = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String phone;


    @Column(name = "url_image")
    private String urlImage;

    @OneToOne
    @JoinColumn(name ="location_region_id" )
    private LocationRegion locationRegion;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public UserDTO toUserDTO(){
        return new UserDTO()
                .setId(id)
                .setUsername(username)
                .setPassword(password)
                .setFullName(fullName)
                .setUrlImage(urlImage)
                .setPhone(phone)
                .setCreatedAt(getCreatedAt())
                .setUpdatedAt(getUpdatedAt())
                .setLocationRegion(locationRegion.toLocationRegionDTO())
                .setRole(role.toRoleDTO());
    }

}
