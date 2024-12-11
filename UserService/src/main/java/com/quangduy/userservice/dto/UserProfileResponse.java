package com.quangduy.userservice.dto;

import com.quangduy.userservice.entity.Role;
import com.quangduy.userservice.entity.User;
import lombok.*;

@Data
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String fullName;
    private String imageUrl;
    private String email;
    private String phone;
    private String address;
    private Role role;
    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.imageUrl = user.getImageUrl();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.role = user.getRole();
    }

}
