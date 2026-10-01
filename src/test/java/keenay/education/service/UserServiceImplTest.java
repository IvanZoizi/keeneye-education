package keenay.education.service;

import keenay.education.dto.auth.LoginDTO;
import keenay.education.dto.auth.RegisterAdminDTO;
import keenay.education.dto.auth.RegisterCustomerDTO;
import keenay.education.dto.auth.RegisterSellerDTO;
import keenay.education.entity.Customers;
import keenay.education.entity.Roles;
import keenay.education.entity.Sellers;
import keenay.education.entity.Users;
import keenay.education.exception.errors.InternalException;
import keenay.education.repository.jpa.CustomersRepository;
import keenay.education.repository.jpa.RolesRepository;
import keenay.education.repository.jpa.SellersRepository;
import keenay.education.repository.jpa.UserRepository;
import keenay.education.security.jwt.JwtService;
import keenay.education.service.email.EmailCreateApplicationService;
import keenay.education.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private CustomersRepository customersRepository;

    @Mock
    private SellersRepository sellersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailCreateApplicationService emailCreateApplicationService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        lenient().when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
    }

    private Users user(Long id, List<Roles> roles) {
        Users user = new Users();
        user.setId(id);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);
        user.setRoles(new ArrayList<>(roles));
        return user;
    }

    private Roles role(Long id, String name) {
        Roles role = new Roles();
        role.setId(id);
        role.setRole(name);
        return role;
    }

    private RegisterAdminDTO adminDto() {
        RegisterAdminDTO dto = new RegisterAdminDTO();
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        return dto;
    }

    private RegisterCustomerDTO customerDto() {
        RegisterCustomerDTO dto = new RegisterCustomerDTO();
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setName("Ivan");
        dto.setSurname("Ivanov");
        return dto;
    }

    private RegisterSellerDTO sellerDto() {
        RegisterSellerDTO dto = new RegisterSellerDTO();
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setName("Petr");
        dto.setSurname("Petrov");
        dto.setAddress("Moscow, Red Square 1");
        dto.setInn("1234567890");
        dto.setDescription("Seller of goods");
        return dto;
    }

    private LoginDTO loginDto() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        return dto;
    }

    @Test
    @DisplayName("Тест registerAdmin новый пользователь")
    public void testRegisterAdminNewUser() throws AuthenticationException {
        Roles adminRole = role(1L, "admin");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(rolesRepository.findByRole("admin")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users u = invocation.getArgument(0);
            assertEquals(EMAIL, u.getEmail());
            assertEquals(ENCODED_PASSWORD, u.getPassword());
            assertTrue(u.getRoles().contains(adminRole));
            u.setId(1L);
            return u;
        });

        String result = userService.registerAdmin(adminDto());

        assertEquals("success", result);
        verify(userRepository).save(any(Users.class));
        verify(passwordEncoder).encode(PASSWORD);
    }

    @Test
    @DisplayName("Тест registerAdmin роль не найдена")
    public void testRegisterAdminRoleNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(rolesRepository.findByRole("admin")).thenReturn(Optional.empty());

        assertThrows(InternalException.class, () -> userService.registerAdmin(adminDto()));
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    @DisplayName("Тест registerAdmin неверный пароль")
    public void testRegisterAdminInvalidPassword() {
        Roles adminRole = role(1L, "admin");
        Users existing = user(1L, List.of(adminRole));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(rolesRepository.findByRole("admin")).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> userService.registerAdmin(adminDto()));
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    @DisplayName("Тест registerAdmin роль уже добавлена")
    public void testRegisterAdminRoleAlreadyAdded() {
        Roles adminRole = role(1L, "admin");
        Users existing = user(1L, List.of(adminRole));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(rolesRepository.findByRole("admin")).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> userService.registerAdmin(adminDto()));
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    @DisplayName("Тест registerAdmin добавление роли существующему пользователю")
    public void testRegisterAdminAddRoleToExistingUser() throws AuthenticationException {
        Roles adminRole = role(1L, "admin");
        Roles customerRole = role(2L, "customer");
        Users existing = user(1L, List.of(customerRole));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(rolesRepository.findByRole("admin")).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = userService.registerAdmin(adminDto());

        assertEquals("success", result);
        assertTrue(existing.getRoles().contains(adminRole));
        verify(userRepository).save(existing);
    }

    @Test
    @DisplayName("Тест registerCustomer новый пользователь")
    public void testRegisterCustomerNewUser() throws AuthenticationException {
        Roles customerRole = role(1L, "customer");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(rolesRepository.findByRole("customer")).thenReturn(Optional.of(customerRole));
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(customersRepository.save(any(Customers.class))).thenAnswer(invocation -> {
            Customers c = invocation.getArgument(0);
            assertEquals("Ivan", c.getName());
            assertEquals("Ivanov", c.getSurname());
            assertNotNull(c.getUser());
            return c;
        });

        String result = userService.registerCustomer(customerDto());

        assertEquals("success", result);
        verify(userRepository).save(any(Users.class));
        verify(customersRepository).save(any(Customers.class));
    }

    @Test
    @DisplayName("Тест registerCustomer роль не найдена")
    public void testRegisterCustomerRoleNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(rolesRepository.findByRole("customer")).thenReturn(Optional.empty());

        assertThrows(InternalException.class, () -> userService.registerCustomer(customerDto()));
        verify(userRepository, never()).save(any(Users.class));
        verify(customersRepository, never()).save(any(Customers.class));
    }

    @Test
    @DisplayName("Тест registerCustomer неверный пароль")
    public void testRegisterCustomerInvalidPassword() {
        Roles customerRole = role(1L, "customer");
        Users existing = user(1L, List.of(customerRole));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(rolesRepository.findByRole("customer")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> userService.registerCustomer(customerDto()));
        verify(customersRepository, never()).save(any(Customers.class));
    }

    @Test
    @DisplayName("Тест registerCustomer роль уже добавлена")
    public void testRegisterCustomerRoleAlreadyAdded() {
        Roles customerRole = role(1L, "customer");
        Users existing = user(1L, List.of(customerRole));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(rolesRepository.findByRole("customer")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> userService.registerCustomer(customerDto()));
        verify(customersRepository, never()).save(any(Customers.class));
    }

    @Test
    @DisplayName("Тест registerSeller новый пользователь")
    public void testRegisterSellerNewUser() throws AuthenticationException {
        Roles sellerRole = role(1L, "seller");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(rolesRepository.findByRole("seller")).thenReturn(Optional.of(sellerRole));
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(sellersRepository.save(any(Sellers.class))).thenAnswer(invocation -> {
            Sellers s = invocation.getArgument(0);
            assertEquals("Petr", s.getName());
            assertEquals("Petrov", s.getSurname());
            assertEquals("Moscow, Red Square 1", s.getAddress());
            assertEquals("1234567890", s.getInn());
            assertEquals("Seller of goods", s.getDescription());
            assertNotNull(s.getUser());
            return s;
        });

        String result = userService.registerSeller(sellerDto());

        assertEquals("success", result);
        verify(userRepository).save(any(Users.class));
        verify(sellersRepository).save(any(Sellers.class));
    }

    @Test
    @DisplayName("Тест registerSeller роль не найдена")
    public void testRegisterSellerRoleNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(rolesRepository.findByRole("seller")).thenReturn(Optional.empty());

        assertThrows(InternalException.class, () -> userService.registerSeller(sellerDto()));
        verify(userRepository, never()).save(any(Users.class));
        verify(sellersRepository, never()).save(any(Sellers.class));
    }

    @Test
    @DisplayName("Тест registerSeller неверный пароль")
    public void testRegisterSellerInvalidPassword() {
        Roles sellerRole = role(1L, "seller");
        Users existing = user(1L, List.of(sellerRole));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(rolesRepository.findByRole("seller")).thenReturn(Optional.of(sellerRole));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> userService.registerSeller(sellerDto()));
        verify(sellersRepository, never()).save(any(Sellers.class));
    }

    @Test
    @DisplayName("Тест registerSeller роль уже добавлена")
    public void testRegisterSellerRoleAlreadyAdded() {
        Roles sellerRole = role(1L, "seller");
        Users existing = user(1L, List.of(sellerRole));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(rolesRepository.findByRole("seller")).thenReturn(Optional.of(sellerRole));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> userService.registerSeller(sellerDto()));
        verify(sellersRepository, never()).save(any(Sellers.class));
    }

    @Test
    @DisplayName("Тест singIn пользователь не найден")
    public void testSignInUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> userService.singIn(loginDto()));
        verifyNoInteractions(jwtService, emailCreateApplicationService);
    }

    @Test
    @DisplayName("Тест singIn неверный пароль")
    public void testSignInInvalidPassword() {
        Roles customerRole = role(1L, "customer");
        Users user = user(1L, List.of(customerRole));

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> userService.singIn(loginDto()));
        verifyNoInteractions(jwtService, emailCreateApplicationService);
    }

    @Test
    @DisplayName("Тест singIn пользователь забанен")
    public void testSignInBannedUser() {
        Roles customerRole = role(1L, "customer");
        Users user = user(1L, List.of(customerRole));
        user.setBannedAt(LocalDateTime.now());

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> userService.singIn(loginDto()));
        verifyNoInteractions(jwtService, emailCreateApplicationService);
    }

    @Test
    @DisplayName("Тест singIn пользователь удалён")
    public void testSignInDeletedUser() {
        Roles customerRole = role(1L, "customer");
        Users user = user(1L, List.of(customerRole));
        user.setDeletedAt(LocalDateTime.now());

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> userService.singIn(loginDto()));
        verifyNoInteractions(jwtService, emailCreateApplicationService);
    }
}