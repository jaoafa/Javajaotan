package com.jaoafa.Javajaotan.Lib;

import com.jaoafa.Javajaotan.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SubAccount {
    private final Set<SubAccount> subAccounts = new HashSet<>();
    private Guild jMSGuild = null;
    private boolean exists = true;
    private boolean isSubAccount = false; // このアカウントがサブアカウント
    private User user = null;
    private SubAccount mainAccount = null;

    public SubAccount(long discordId) {
        try {
            this.jMSGuild = Main.getJDA().getGuildById(597378876556967936L);

            MySQLDBManager manager = Main.MySQLDBManager;
            Connection conn = manager.getConnection();

            try {
                this.user = Main.getJDA().retrieveUserById(discordId).complete();
            } catch (ErrorResponseException e) {
                exists = false;
                return;
            }

            // 自分がサブアカウントかどうかを取得する
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM subaccount WHERE disid = ?");
            stmt.setLong(1, discordId);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                this.mainAccount = new SubAccount(res.getLong("main_disid"));
            }
            res.close();
            stmt.close();

            // すべてのサブアカウントを取得する
            PreparedStatement stmt_sub = conn.prepareStatement("SELECT * FROM subaccount WHERE main_disid = ?");
            stmt_sub.setLong(1, discordId);
            ResultSet res_sub = stmt_sub.executeQuery();
            while (res_sub.next()) {
                subAccounts.add(new SubAccount(res.getLong("disid")));
            }
            res_sub.close();
            stmt_sub.close();
            isSubAccount = !subAccounts.isEmpty();
        } catch (SQLException e) {
            e.printStackTrace();
            exists = false;
        }
    }

    public boolean setMainAccount(@NotNull SubAccount mainAccount) {
        Role SubAccountRole = jMSGuild.getRoleById(753047225751568474L);
        if (SubAccountRole == null) {
            return false;
        }
        try {
            MySQLDBManager manager = Main.MySQLDBManager;
            Connection conn = manager.getConnection();

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO subaccount (name, discriminator, disid, main_name, main_discriminator, main_disid, created_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);");
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getDiscriminator());
            stmt.setLong(3, user.getIdLong());
            stmt.setString(4, mainAccount.getUser().getName());
            stmt.setString(5, mainAccount.getUser().getDiscriminator());
            stmt.setLong(6, mainAccount.getUser().getIdLong());
            stmt.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        jMSGuild.addRoleToMember(user.getId(), SubAccountRole).queue();
        return true;
    }

    public boolean removeMainAccount() {
        Role SubAccountRole = jMSGuild.getRoleById(753047225751568474L);
        if (SubAccountRole == null) {
            return false;
        }
        try {
            MySQLDBManager manager = Main.MySQLDBManager;
            Connection conn = manager.getConnection();

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM subaccount WHERE disid = ?");
            stmt.setLong(1, user.getIdLong());
            stmt.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        jMSGuild.removeRoleFromMember(user.getId(), SubAccountRole).queue();
        return true;
    }

    public boolean isExists() {
        return exists;
    }

    public boolean isSubAccount() {
        return isSubAccount;
    }

    public User getUser() {
        return user;
    }

    public SubAccount getMainAccount() {
        return mainAccount;
    }

    public Set<SubAccount> getSubAccounts() {
        return subAccounts;
    }
}
