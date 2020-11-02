package app.zingo.employeemanagements.Custom.Floating;

public enum RFABSize {
    NORMAL(0, 56),
    MINI(1, 40);
    int code;
    int dpSize;

    RFABSize(int code, int dpSize) {
        this.code = code;
        this.dpSize = dpSize;
    }

    public static RFABSize getRFABSizeByCode( int code) {
        for ( RFABSize rfabSize : RFABSize.values()) {
            if (code == rfabSize.code) {
                return rfabSize;
            }
        }
        return NORMAL;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getDpSize() {
        return dpSize;
    }

    public void setDpSize(int dpSize) {
        this.dpSize = dpSize;
    }
}
