package flab.commercemarket.common.config.auth;

import flab.commercemarket.domain.user.repository.UserRepository;
import flab.commercemarket.domain.user.vo.Role;
import flab.commercemarket.domain.user.vo.SessionUser;
import flab.commercemarket.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        User user = handleUserAuthentication(oAuth2User);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                oAuth2User.getAttributes(),
                userNameAttributeName);
    }

    private User handleUserAuthentication(OAuth2User oAuth2User) {
        Optional<User> optionalUser = userRepository.findByEmail(oAuth2User.getAttribute("email"));

        User newUser = optionalUser.orElseGet(() -> {
            User user = toUser(oAuth2User);
            return userRepository.save(user);
        });

        httpSession.setAttribute(newUser.getName(), new SessionUser(newUser));
        return newUser;
    }

    private User toUser(OAuth2User oAuth2User) {
        return User.builder()
                .name(oAuth2User.getAttribute("name"))
                .email(oAuth2User.getAttribute("email"))
                .picture(oAuth2User.getAttribute("picture"))
                .role(Role.USER)
                .build();
    }
}
