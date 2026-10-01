package keenay.education.service.impl;

import keenay.education.dto.auth.LoginDTO;
import keenay.education.dto.auth.RegisterAdminDTO;
import keenay.education.dto.auth.RegisterCustomerDTO;
import keenay.education.dto.auth.RegisterSellerDTO;
import keenay.education.dto.security.JwtAutorizeToken;
import keenay.education.entity.*;
import keenay.education.exception.errors.InternalException;
import keenay.education.repository.jpa.CustomersRepository;
import keenay.education.repository.jpa.RolesRepository;
import keenay.education.repository.jpa.SellersRepository;
import keenay.education.repository.jpa.UserRepository;
import keenay.education.repository.tarantool.JwtInfoRepository;
import keenay.education.security.jwt.JwtService;
import keenay.education.service.UserService;
import keenay.education.service.email.EmailCreateApplicationService;
import keenay.education.utils.UtilsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final CustomersRepository customersRepository;
    private final SellersRepository sellersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailCreateApplicationService emailCreateApplicationService;
    private final JwtInfoRepository jwtInfoRepository;

    private final Integer daysExpired;

    public UserServiceImpl(UserRepository userRepository,
                           RolesRepository rolesRepository,
                           CustomersRepository customersRepository,
                           SellersRepository sellersRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           EmailCreateApplicationService emailCreateApplicationService,
                           JwtInfoRepository jwtInfoRepository,
                           @Value("${spring.security.days}") Integer daysExpired) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.customersRepository = customersRepository;
        this.sellersRepository = sellersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailCreateApplicationService = emailCreateApplicationService;
        this.jwtInfoRepository = jwtInfoRepository;
        this.daysExpired = daysExpired;
    }


    private JwtAutorizeToken createJwtIngo(Users user) {
        JwtAutorizeToken jwtAutorizeToken = jwtService.generateAuthToken(user.getEmail(), user.getRoles());
        JwtInfo jwtInfo = JwtInfo.builder()
                .tokenHash(UtilsService.sha256(jwtAutorizeToken.getToken()))
                .token(jwtAutorizeToken.getToken())
                .userId(user.getId())
                .customerId(user.getCustomer() == null?null:user.getCustomer().getId())
                .sellerId(user.getSeller() == null?null:user.getSeller().getId())
                .createdAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .expiredAt(LocalDateTime.now().plusDays(this.daysExpired).toEpochSecond(ZoneOffset.UTC))
                .deletedAt(null)
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        jwtInfoRepository.save(jwtInfo);
        return jwtAutorizeToken;
    }

    private Users createUser(String email, String password, List<Roles> roles) {
        Users user = Users.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .build();
        return userRepository.save(user);
    }

    private void validatePasswordAndRole(String password, String passwordForCheck,
                       List<Roles> rolesList, Roles customerRole) throws AuthenticationException {
        if  (!passwordEncoder.matches(password, passwordForCheck)) {
            throw new AuthenticationException("Invalid password.");
        }
        if (UtilsService.in(rolesList, customerRole)) {
            throw new AuthenticationException("The role has already been added");
        }
    }

    private Customers createCustomers(String name, String surname, Long userId) throws AuthenticationException {
        Customers customers = Customers.builder()
                .name(name)
                .surname(surname)
                .userId(userId)
                .build();
        return customersRepository.save(customers);
    }

    private Sellers createSellers(String name, String surname, String address, String inn, String description, Long userId) throws AuthenticationException {
        Sellers seller = Sellers.builder()
                .name(name)
                .surname(surname)
                .address(address)
                .inn(inn)
                .description(description)
                .userId(userId)
                .build();
        return sellersRepository.save(seller);
    }

    @Override
    public String registerAdmin(RegisterAdminDTO registerAdminDTO) throws AuthenticationException {

        Optional<Users> usersOptional = userRepository.findByEmail(registerAdminDTO.getEmail());
        Roles adminRole = rolesRepository.findByRole("admin").orElseThrow(() -> new InternalException("Role not found"));
        if (usersOptional.isEmpty()) {
            this.createUser(registerAdminDTO.getEmail(),
                    registerAdminDTO.getPassword(),
                    List.of(adminRole));
        } else {
            Users currentUser = usersOptional.get();
            List<Roles> rolesList = currentUser.getRoles();
            validatePasswordAndRole(registerAdminDTO.getPassword(), currentUser.getPassword(), rolesList, adminRole);
            rolesList.add(adminRole);
            currentUser.setRoles(rolesList);
            userRepository.save(currentUser);
        }
        return "success";
    }

    @Override
    public String registerCustomer(RegisterCustomerDTO registerCustomerDTO) throws AuthenticationException {
        Optional<Users> usersOptional = userRepository.findByEmail(registerCustomerDTO.getEmail());
        Roles customerRole = rolesRepository.findByRole("customer").orElseThrow(() -> new InternalException("Role not found"));
        if (usersOptional.isEmpty()) {
            Users user = this.createUser(registerCustomerDTO.getEmail(),
                    registerCustomerDTO.getPassword(),
                    List.of(customerRole));
            this.createCustomers(
                    registerCustomerDTO.getName(),
                    registerCustomerDTO.getSurname(),
                    user.getId()
            );

        } else {
            Users currentUser = usersOptional.get();
            List<Roles> rolesList = currentUser.getRoles();
            validatePasswordAndRole(registerCustomerDTO.getPassword(), currentUser.getPassword(), rolesList, customerRole);
            rolesList.add(customerRole);
            currentUser.setRoles(rolesList);
            this.createCustomers(
                    registerCustomerDTO.getName(),
                    registerCustomerDTO.getSurname(),
                    userRepository.save(currentUser).getId()
            );
        }
        return "success";
    }

    @Override
    public String registerSeller(RegisterSellerDTO registerSellerDTO) throws AuthenticationException {
        Optional<Users> usersOptional = userRepository.findByEmail(registerSellerDTO.getEmail());
        Roles sellerRole = rolesRepository.findByRole("seller").orElseThrow(() -> new InternalException("Role not found"));
        if (usersOptional.isEmpty()) {
            Users user = this.createUser(registerSellerDTO.getEmail(),
                    registerSellerDTO.getPassword(),
                    List.of(sellerRole));
            this.createSellers(
                    registerSellerDTO.getName(),
                    registerSellerDTO.getSurname(),
                    registerSellerDTO.getAddress(),
                    registerSellerDTO.getInn(),
                    registerSellerDTO.getDescription(),
                    user.getId()
            );

        } else {
            Users currentUser = usersOptional.get();
            List<Roles> rolesList = currentUser.getRoles();
            validatePasswordAndRole(registerSellerDTO.getPassword(), currentUser.getPassword(), rolesList, sellerRole);
            rolesList.add(sellerRole);
            currentUser.setRoles(rolesList);
            this.createSellers(
                    registerSellerDTO.getName(),
                    registerSellerDTO.getSurname(),
                    registerSellerDTO.getAddress(),
                    registerSellerDTO.getInn(),
                    registerSellerDTO.getDescription(),
                    userRepository.save(currentUser).getId()
            );
        }
        return "success";
    }

    @Override
    public JwtAutorizeToken singIn(LoginDTO loginDTO) throws AuthenticationException {
        Users user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("The user with this ID was not found."));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid password.");
        }
        if (user.getBannedAt() != null) {
            throw new AuthenticationException("The user has been banned.");
        }
        if (user.getDeletedAt() != null) {
            throw new AuthenticationException("The user has been deleted.");
        }

        return createJwtIngo(user);
    }

    @Override
    public String logout(String token) {

        String hashToken = UtilsService.sha256(token);

        jwtInfoRepository.findById(hashToken).ifPresent(info -> {
            info.setDeletedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            jwtInfoRepository.save(info);
        });

        return "success";
    }
}
