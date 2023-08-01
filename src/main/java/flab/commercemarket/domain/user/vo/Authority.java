package flab.commercemarket.domain.user.vo;

public enum Authority {
    BUYER(1),
    SELLER(2),
    ADMIN(3);

    private final int authorityValue;

    Authority(int authorityValue) {
        this.authorityValue = authorityValue;
    }

    public int getAuthorityValue() {
        return authorityValue;
    }

    public static Authority valueOf(int authorityValue) {
        switch (authorityValue) {
            case 1:
                return BUYER;
            case 2:
                return SELLER;
            case 3:
                return ADMIN;
            default:
                throw new AssertionError("Unknown authorityValue : " + authorityValue);
        }
    }
}

