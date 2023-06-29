package javharbek.starter.exceptions;

public class ServiceGatewayException extends AppException {

    public static String ERROR_CODE_COMMON_001 = "ERROR_CODE_COMMON_001";
    public static String ERROR_TITLE_COMMON_001 = "ERROR_TITLE_COMMON_001";
    public static String ERROR_CODE_SG_MVD_001 = "ERROR_CODE_SG_001";
    public static String ERROR_TITLE_SG_MVD_001 = "ERROR_TITLE_SG_001";
    public static String ERROR_CODE_SG_MVD_002 = "ERROR_CODE_SG_002";
    public static String ERROR_TITLE_SG_MVD_002 = "ERROR_TITLE_SG_002";
    public static String ERROR_CODE_SG_MGW_001 = "ERROR_CODE_SG_MGW_001";
    public static String ERROR_TITLE_SG_MGW_001 = "ERROR_TITLE_SG_MGW_001";
    public static String ERROR_CODE_SG_MGW_002 = "ERROR_CODE_SG_MGW_002";
    public static String ERROR_TITLE_SG_MGW_002 = "ERROR_TITLE_SG_MGW_002";
    public static String ERROR_CODE_SG_MGW_TIN_003 = "ERROR_CODE_SG_MGW_TIN_003";
    public static String ERROR_TITLE_SG_MGW_TIN_003 = "ERROR_TITLE_SG_MGW_TIN_003";

    public String code;
    public String errorData;

    public ServiceGatewayException(String message) {
        super(message);
    }

    public ServiceGatewayException(String message,String code) {
        super(message);
        this.code = code;
    }

    public ServiceGatewayException(String message,String code,String errorData) {
        super(message);
        this.code = code;
        this.errorData = errorData;
    }

    @Override
    public String code() {
        if (code == null) {
            this.code = this.getClass().getSimpleName();
        }
        return this.code;
    }
}
