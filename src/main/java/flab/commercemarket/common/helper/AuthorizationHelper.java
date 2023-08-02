package flab.commercemarket.common.helper;

import flab.commercemarket.common.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationHelper {

    public void checkUserAuthorization(long ownerUserId, long loginUserId) {
        if (ownerUserId != loginUserId) {
            log.info("dataUserId = {}, loginUserId = {}", ownerUserId, loginUserId);
            throw new ForbiddenException("유저 권한정보가 일치하지 않음");
        }
    }
}
