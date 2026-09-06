package com.SecurityApp.services;

import com.SecurityApp.dto.SignupDTO;
import com.SecurityApp.dto.UserDTO;
import com.SecurityApp.entities.User;
import com.SecurityApp.respositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private  final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User with email " + username + " not found"));
    }
    public UserDTO signup(SignupDTO signupDTO){
        Optional<User> user=userRepository.findByEmail(signupDTO.getEmail());
        if(user.isPresent()){
            throw new BadCredentialsException("User with this email is already exist" + signupDTO.getEmail());
        }
        User toBeCreatedUser=modelMapper.map(signupDTO,User.class);
        User tosaveUser=userRepository.save(toBeCreatedUser);
        return  modelMapper.map(tosaveUser,UserDTO.class);
    }

}
