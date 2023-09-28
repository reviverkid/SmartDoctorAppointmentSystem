package thereviverkid.atwebpages.medcare.DataRetrievalClass;

public class UserDetails {
    private String Email, FirstName, LastName, MobileNo, UserType, UserBalance,UserId;

    public UserDetails() {
    }

    public UserDetails(String email, String firstName, String lastName, String mobileNo, String userType, String userBalance, String userId) {
        Email = email;
        FirstName = firstName;
        LastName = lastName;
        MobileNo = mobileNo;
        UserType = userType;
        UserBalance = userBalance;
        UserId = userId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getUserBalance() {
        return UserBalance;
    }

    public void setUserBalance(String userBalance) {
        UserBalance = userBalance;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}