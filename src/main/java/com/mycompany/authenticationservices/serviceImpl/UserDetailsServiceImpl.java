package com.mycompany.authenticationservices.serviceImpl;

import java.util.*;
import java.util.stream.Collectors;


import com.mycompany.authenticationservices.dto.SignupDTO;
import com.mycompany.authenticationservices.dto.UserDTO;
import com.mycompany.authenticationservices.entity.UserEntity;
import com.mycompany.authenticationservices.exception.DataNotFoundException;
import com.mycompany.authenticationservices.exception.DuplicateException;
import com.mycompany.authenticationservices.mapper.AppMapper;
import com.mycompany.authenticationservices.repository.UserRepository;
import com.mycompany.authenticationservices.service.RoleService;
import com.mycompany.authenticationservices.service.UserService;
import com.mycompany.authenticationservices.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import org.springframework.util.ObjectUtils;

/**
 * @author pervez
 * @version 1.0
 * @since 4 August, 2022
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public UserDetails loadUserByUsername(String usernameValue) {
        Optional<UserEntity> user = getUserByUsername(usernameValue);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        detailsChecker.check(user.get());
        return user.get();
    }


    /**
     * @param usernameValue username or email
     * @return Optional User
     */
    private Optional<UserEntity> getUserByUsername(String usernameValue) {
        // trim username value
        var username = StringUtils.trimToNull(usernameValue);
        if (StringUtils.isEmpty(username)) {
            return Optional.empty();
        }
        return username.contains("@") ? userRepository.findActiveByEmail(username) : userRepository.findActiveByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream().map(AppMapper::userEntityToDto).toList();
    }

    @Override
    public List<UserDTO> getByAccountId(String accountId) {
        return userRepository.findByAccountId(accountId).stream().map(AppMapper::userEntityToDto).toList();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO createUser(SignupDTO signupDTO) {
        if (userRepository.findByUsername(signupDTO.getUsername()).isPresent()) {
            throw new DuplicateException("Username Already exist !!");
        }
        if (userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new DuplicateException("Email Already exist !!");
        }

        UserEntity userEntity = AppMapper.userDtoToEntity(signupDTO)
                .setPassword(signupDTO.getPassword())
                .setRoles(signupDTO.getRoles().stream().map(roleEnum -> roleService.getByName(roleEnum)).collect(Collectors.toSet()));

        return AppMapper.userEntityToDto(userRepository.save(userEntity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return null;
    }

    /**
     * Delete UserEntity
     *
     * @param id - user id
     * @author pervez
     * @version 1.0
     * @since 04 August, 2022
     */
    @Override
    public void deleteById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("UserEntity not found for this account id " + id));
        userRepository.deleteById(id);

    }

    /**
     * Get UserEntity by id
     *
     * @param id - user id
     * @return user Object
     * @author pervez
     * @version 1.0
     * @since 04 August, 2022
     */
    @Override
    public UserDTO getById(Long id) {
        return AppMapper.userEntityToDto(userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("UserEntity not found for this id " + id)));
    }

    /**
     * Update UserEntity
     *
     * @param newUser - updated user object
     * @param id      - user id
     * @return user Object
     * @author pervez
     * @version 1.0
     * @since 04 August, 2022
     */
    @Override
    public UserDTO updateUser(UserDTO newUser, Long id) {
		checkUpdatedUserIsExist(newUser);
		return AppMapper.userEntityToDto(userRepository.findById(id) //
                .map(user -> {
                    user.setFirstName(StringUtils.trimToNull(newUser.getFirstName()));
                    user.setLastName(StringUtils.trimToNull(newUser.getLastName()));
                    return userRepository.save(user);
                }).orElseThrow(() -> new DataNotFoundException("UserEntity not found for this id" + id)));
    }

    /**
     * Partially Update UserEntity
     *
     * @param newUser -partially updated user object
     * @param id      - user id
     * @return user Object
     * @author pervez
     * @version 1.0
     * @since 04 August, 2022
     */
    @Override
    public UserDTO patchUser(UserDTO newUser, Long id) {
		checkUpdatedUserIsExist(newUser);
        return AppMapper.userEntityToDto(userRepository.findById(id) //
                .map(user -> {
                    if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newUser.getFirstName()))) {
                        user.setFirstName(newUser.getFirstName());
                    }
                    if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newUser.getLastName()))) {
                        user.setLastName(newUser.getLastName());
                    }
                    if (!ObjectUtils.isEmpty(newUser.getRoles()) && newUser.getRoles().size()>0) {
                        user.getRoles().addAll(newUser.getRoles().stream().distinct().map(roleEnum -> roleService.getByName(roleEnum)).collect(Collectors.toSet()));
                    }
                    return userRepository.save(user);
                }).orElseThrow(() -> new DataNotFoundException("UserEntity not found for this id" + id)));
    }

    /**
     * Get All Users
     *
     * @param pageable - Pageable object
     * @return Page of user Object
     * @author pervez
     * @version 1.0
     * @since 04 August, 2022
     */
    @Override
    public Page<UserDTO> getUsers(Pageable pageable) {
        final Boolean isPaginationActive = Utils.isPaginationActive(pageable);
        List<UserEntity> users_ = new ArrayList<>();
        long totalElements = 0;
        if (isPaginationActive) {
            final Page<UserEntity> users = userRepository.findAll(pageable);
            return new PageImpl<>(users.getContent().stream().map(AppMapper::userEntityToDto).toList(), pageable, users.getTotalElements());
        } else {
            users_ = userRepository.findAll();
            final Pageable pageWithElements = PageRequest.of(0, users_.size());
            totalElements = users_.size();
            return new PageImpl<>(users_.stream().map(AppMapper::userEntityToDto).toList(), pageWithElements, totalElements);
        }
    }

    private void checkUpdatedUserIsExist(UserDTO userDTO) {
        if (userRepository.findByUsernameAndIdNot(userDTO.getUsername(), userDTO.getId()).isPresent()) {
            throw new DuplicateException("Username Already exist !!");
        }
        if (userRepository.findByEmailAndIdNot(userDTO.getEmail(), userDTO.getId()).isPresent()) {
            throw new DuplicateException("Email Already exist !!");
        }
    }

}
