import java.sql.*;
public class BankService {

    public long createAccount(String name, String phone, String password) throws SQLException {
        String sql = "INSERT INTO users(name, phone_num, password) VALUES (?, ?, ?)";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, password);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public long login(long accountNum, String password) {
        String sql = "SELECT * FROM users WHERE account_num=? AND password=?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, accountNum);
            ps.setString(2, password);

            rs = ps.executeQuery();
            if (rs.next()) 
                return accountNum;
            
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void deposit(long accNum, double amount) throws SQLException {
        if(amount<=0){
            System.out.println("Deposit Failed! Invalid Credentials (Expected amount greater than ZERO).");
            return;
        }
        String updateBalance = "UPDATE users SET balance = balance + ? WHERE account_num = ?";
        String insertTxn = "INSERT INTO transactions(account_num, type, amount) VALUES (?, 'deposit', ?)";

        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            ps1 = con.prepareStatement(updateBalance);
            ps2 = con.prepareStatement(insertTxn);

            ps1.setDouble(1, amount);
            ps1.setLong(2, accNum);
            ps1.executeUpdate();

            ps2.setLong(1, accNum);
            ps2.setDouble(2, amount);
            ps2.executeUpdate();

            con.commit();
            System.out.println("Deposit successful!");

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("Deposit failed, rollback done.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            try { if (ps2 != null) ps2.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps1 != null) ps1.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public void withdraw(long accNum, double amount) throws SQLException {
        if(amount<=0){
            System.out.println("Withdrawal Failed! Invalid Credentials (Expected amount greater than ZERO).");
            return;
        }
        String updateBalance = "UPDATE users SET balance = balance - ? WHERE account_num = ?";
        String insertTxn = "INSERT INTO transactions(account_num, type, amount) VALUES (?, 'withdraw', ?)";

        Connection con = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            psUpdate = con.prepareStatement("SELECT balance FROM users WHERE account_num = ?");
            psUpdate.setLong(1, accNum);
            rs = psUpdate.executeQuery();

            double currentBalance;
            if (rs.next()) {
                currentBalance = rs.getDouble("balance");
                if (currentBalance - amount < 1000) {
                    System.out.println("Withdrawal failed: Minimum balance 1000 required!\nCurrent Balance: "+currentBalance);
                    return;
                }
            } else {
                System.out.println("Account not found!");
                return;
            }
            rs.close();
            psUpdate.close();

            psUpdate = con.prepareStatement(updateBalance);
            psUpdate.setDouble(1, amount);
            psUpdate.setLong(2, accNum);
            psUpdate.executeUpdate();

            psInsert = con.prepareStatement(insertTxn);
            psInsert.setLong(1, accNum);
            psInsert.setDouble(2, amount);
            psInsert.executeUpdate();

            con.commit();
            System.out.println("Withdrawal successful!\nCurrent Balance: "+(currentBalance - amount));

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("Withdrawal failed, rollback done.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psUpdate != null) psUpdate.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psInsert != null) psInsert.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public double getBalance(long accNum) {
        String sql = "SELECT balance FROM users WHERE account_num=?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, accNum);

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return -1;
    }
    public void showTransactions(long accNum) {
        String sql = "SELECT * FROM transactions WHERE account_num=? ORDER BY transaction_date DESC";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, accNum);

            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(
                    rs.getLong("transaction_id") + " | " +
                    rs.getString("type") + " | " +
                    rs.getDouble("amount") + " | " +
                    rs.getTimestamp("transaction_date")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
