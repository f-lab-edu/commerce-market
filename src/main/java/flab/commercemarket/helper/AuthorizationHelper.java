package flab.commercemarket.helper;

import flab.commercemarket.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationHelper {

    public void checkUserAuthorization(long cartUserId, long loginUserId) {
        if (cartUserId != loginUserId) {
            log.info("cartUserId = {}, loginUserId = {}", cartUserId, loginUserId);
            throw new ForbiddenException("유저 권한정보가 일치하지 않음");
        }
    }
}
