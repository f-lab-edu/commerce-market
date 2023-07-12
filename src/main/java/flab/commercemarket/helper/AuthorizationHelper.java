package flab.commercemarket.helper;

import flab.commercemarket.exception.ForbiddenException;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHelper {

    public void checkUserAuthorization(long cartUserId, long loginUserId) {
        if (cartUserId != loginUserId) {
            throw new ForbiddenException("유저권한정보가 일치하지 않음");
        }
    }
}
