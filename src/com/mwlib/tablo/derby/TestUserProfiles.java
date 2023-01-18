package com.mwlib.tablo.derby;

import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.DefaultProfiles;
import com.mycompany.common.UserProfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 17.12.14
 * Time: 17:31
 */
public class TestUserProfiles {

    public TestUserProfiles() throws Exception {
        //Здесь инициализируются таблицы для профиля и накатываются обновления для актуализации структуры БД
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();

//            if (!isExists(conn, USERS))
//            {
//                String query ="CREATE TABLE "+USERS+" (profileId INTEGER,PRIMARY KEY (uid) )";
//                stmt.execute(query);
//            }

            //20480
            if (!isExists(conn, DefaultProfiles.PROFILES)) {
                String query = "CREATE TABLE " + DefaultProfiles.PROFILES + " (uid VARCHAR (255) NOT NULL,profileId INTEGER NOT NULL,profileName VARCHAR(255)  NOT NULL, profile VARCHAR (32672),LONGPROFILE CLOB (8192 K),PRIMARY KEY (uid) )";
//                String query ="CREATE TABLE "+DefaultProfiles.PROFILES+" (uid VARCHAR (255) NOT NULL,profileId INTEGER NOT NULL,profileName VARCHAR(255)  NOT NULL, profile VARCHAR (32672),PRIMARY KEY (uid) )";
                stmt.execute(query);
                stmt.execute("CREATE UNIQUE INDEX " + DefaultProfiles.PROFILES + "_IX ON " + DefaultProfiles.PROFILES + " (profileId)");
            } else if (!isColumnExist(conn, DefaultProfiles.PROFILES, "LONGPROFILE")) {
                String query = "ALTER TABLE " + DefaultProfiles.PROFILES + " ADD COLUMN LONGPROFILE CLOB (8192 K)";
                stmt.execute(query);
            }


            //PATCH 1.01
            {
                // Проверка на существование таблицы USERS в БД
                if (!isExists(conn, DefaultProfiles.USERS)) {
                    String query = "CREATE TABLE " + DefaultProfiles.USERS + " (USERID DECIMAL NOT NULL,\n" +
                            "    USERSETTINGS CLOB (8192 K), PRIMARY KEY (USERID) )";
//                String query ="CREATE TABLE "+DefaultProfiles.PROFILES+" (uid VARCHAR (255) NOT NULL,profileId INTEGER NOT NULL,profileName VARCHAR(255)  NOT NULL, profile VARCHAR (32672),PRIMARY KEY (uid) )";
                    stmt.execute(query);
                    stmt.execute("CREATE UNIQUE INDEX " + DefaultProfiles.USERS + "_IX ON " + DefaultProfiles.USERS + " (USERID)");

                }
                //Проверка на существование таблицы NEW_PROFILES в БД
                if (!isExists(conn, DefaultProfiles.NEW_PROFILES)) {
                    String query = "CREATE TABLE " + DefaultProfiles.NEW_PROFILES + " (PROFILEID DECIMAL NOT NULL,\n" +
                            "    PROFILE_NAME VARCHAR(250) NOT NULL,\n" +
                            "    PROFILE_DESCRIPTOR CLOB (8192 K), PRIMARY KEY (PROFILEID))";
//                String query ="CREATE TABLE "+DefaultProfiles.PROFILES+" (uid VARCHAR (255) NOT NULL,profileId INTEGER NOT NULL,profileName VARCHAR(255)  NOT NULL, profile VARCHAR (32672),PRIMARY KEY (uid) )";
                    stmt.execute(query);
                    stmt.execute("CREATE UNIQUE INDEX " + DefaultProfiles.NEW_PROFILES + "_IX ON " + DefaultProfiles.NEW_PROFILES + " (PROFILEID)");

                }
                //Проверка на существование таблицы USERS_PROFILES в БД
                if (!isExists(conn, DefaultProfiles.USERS_PROFILES)) {
                    String query = "CREATE TABLE " + DefaultProfiles.USERS_PROFILES + " ( USERID DECIMAL NOT NULL,\n" +
                            "    PROFILEID DECIMAL NOT NULL,\n" +
                            "    MODIFIED_PROFILE CLOB (8192 K), PRIMARY KEY (USERID,PROFILEID) )";
//                String query ="CREATE TABLE "+DefaultProfiles.PROFILES+" (uid VARCHAR (255) NOT NULL,profileId INTEGER NOT NULL,profileName VARCHAR(255)  NOT NULL, profile VARCHAR (32672),PRIMARY KEY (uid) )";
                    stmt.execute(query);
                    //  stmt.execute("CREATE UNIQUE INDEX " + DefaultProfiles.USERS_PROFILES + "_IX ON " + DefaultProfiles.USERS_PROFILES + " (USERID, PROFILEID)");


                    stmt.execute("ALTER TABLE " + DefaultProfiles.USERS_PROFILES +
                            " ADD FOREIGN KEY (USERID)" +
                            " REFERENCES USERS(USERID)");

                    stmt.execute("ALTER TABLE " + DefaultProfiles.USERS_PROFILES +
                            " ADD FOREIGN KEY (PROFILEID)" +
                            " REFERENCES NEW_PROFILES(PROFILEID)");


                    copyExistingProfiles();

                }

                // if (!isProfileExist_new(DefaultProfiles.defProfile))
                //    insertOrUpdateProfiles_new(DefaultProfiles.defProfile, "");
            }

            // if (!isProfileExist(DefaultProfiles.defProfile))
            //     insertOrUpdateProfiles2(DefaultProfiles.DEF_USER_ID, DefaultProfiles.defProfile, "");
        } finally {
            DbUtil.closeAll(null, stmt, conn, true);
        }


    }

    public void copyExistingProfiles() throws Exception {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            conn.setAutoCommit(false);
// Добавляем пользователей
            BigDecimal[] users = {new BigDecimal("" + 2821)/*Центр*/, new BigDecimal("" + 2848)/*Горьковская*/, new BigDecimal("" + 3041)/*Северная*/, new BigDecimal("" + 3160)/*Северо-Кавказская*/, new BigDecimal("" + 2871)/*Приволжская*/, new BigDecimal("" + 2947)/*Западно-Сибирская*/, new BigDecimal("" + 3171)/*Восточно-Сибирская*/};

            stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.USERS + "(USERID, USERSETTINGS) values(?,?)"); // TODO одставить соответствующие значения для идентификаторов пользователей
            for (int i = 0; i < users.length; i++) {
                stmt.setBigDecimal(1, users[i]);
                stmt.setNull(2, Types.CLOB);
                stmt.addBatch();
            }
            stmt.executeBatch();


            //Обрабатываем профили для каждого пользователя в отдельности
            int id = -1;

            //Выбираем все профили из таблицы PROFILES
            stmt = conn.prepareStatement("select PROFILENAME,LONGPROFILE from " + DefaultProfiles.PROFILES, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);

            rs = stmt.executeQuery();


            for (int i = 0; i < users.length; i++) {
                if (id < 0) id = getNextProfileId_new();
                // Если это пользователь Северной или Приволжской дороги, то привязываем к ним все существующие профили из таблицы PROFILES,при этом создается копия профиля, т.е. не множественной привязки одного профиля к нескольким пользователям
                if (users[i].intValue() == 3041 || users[i].intValue() == 2871) {

                    System.out.println(id);
                    while (rs.next()) {
                        stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.NEW_PROFILES + "(PROFILEID, PROFILE_NAME,PROFILE_DESCRIPTOR) values (?,?,?)"); // TODO оставить соответствующие значения для идентификаторов пользователей


                        stmt.setBigDecimal(1, new BigDecimal("" + id));
                        stmt.setString(2, rs.getString("PROFILENAME"));
                        stmt.setClob(3, rs.getClob("LONGPROFILE"));
                        stmt.executeUpdate();

                        stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.USERS_PROFILES + "(USERID, PROFILEID, MODIFIED_PROFILE) values (?,?,?)");
                        stmt.setBigDecimal(1, users[i]);
                        stmt.setBigDecimal(2, new BigDecimal("" + id));
                        stmt.setNull(3, Types.CLOB);
                        stmt.executeUpdate();


                        id++;
                    }
                    rs.beforeFirst();

                }
                //Если это другие пользователи, то привязываем к ним пустой профиль с созданием копии этого профиля для каждого пользователя
                else {
                    stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.NEW_PROFILES + "(PROFILEID, PROFILE_NAME,PROFILE_DESCRIPTOR) values (?,?,?)"); // TODO оставить соответствующие значения для идентификаторов пользователей


                    stmt.setBigDecimal(1, new BigDecimal("" + id));
                    stmt.setString(2, "default");
                    stmt.setAsciiStream(3, new ByteArrayInputStream("".getBytes("UTF8")), "".getBytes("UTF8").length);
                    // stmt.setNull(3, Types.CLOB);
                    stmt.executeUpdate();

                    stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.USERS_PROFILES + "(USERID, PROFILEID, MODIFIED_PROFILE) values (?,?,?)");
                    stmt.setBigDecimal(1, users[i]);
                    stmt.setBigDecimal(2, new BigDecimal("" + id));
                    stmt.setNull(3, Types.CLOB);
                    stmt.executeUpdate();
                    id++;
                }


            }
            //  stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.USERS_PROFILES + "(USERID, PROFILEID, MODIFIED_PROFILE) select 123, PROFILEID, null from NEW_PROFILES where PROFILE_NAME=''"); // TODO оставить соответствующие значения для идентификаторов пользователей
            //  stmt.executeUpdate();

            //  stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.USERS_PROFILES + "(USERID, PROFILEID, MODIFIED_PROFILE) select 123, PROFILEID, null from NEW_PROFILES where PROFILE_NAME=''");
            //  stmt.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
            conn.rollback();

        } finally {
            if (conn != null)
                conn.setAutoCommit(true);
            DbUtil.closeAll(rs, stmt, conn, true);
        }


    }

//    public void insertOrUpdateProfiles(String userId,UserProfile userProfile,String descriptor) throws Exception
//    {
//
//        Connection conn=null;
//        PreparedStatement stmt=null;
//        ResultSet rs = null;
//
//        try {
//            conn= DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
//
//            stmt = conn.prepareStatement("UPDATE "+DefaultProfiles.PROFILES+" SET profile=?,profileName=? WHERE profileId=?");
//            stmt.setString(1, descriptor);
//            stmt.setString(2, userProfile.getProfileName());
//            stmt.setInt(3, userProfile.getProfileId());
//            int res=stmt.executeUpdate();
//            if (res==0)
//            {
//                stmt = conn.prepareStatement("INSERT into "+DefaultProfiles.PROFILES+"( uid,profileId,profileName,profile) values(?,?,?,?)");
//                stmt.setString(1, userId);
//                stmt.setInt(2, userProfile.getProfileId());
//                stmt.setString(3, userProfile.getProfileName());
//                stmt.setString(4, descriptor);
//                stmt.executeUpdate();
//            }
//        }
//        finally
//        {
//            DbUtil.closeAll(rs,stmt,conn,true);
//        }
//
//    }

    /**
     * Метод БД сохранения или модификации профиля ПО ИДЕНТИФИКАТОРУ ПРОФИЛЯ. ИДЕНТИФИКАТОР ПОЛЬЗОВАТЕЛЯ В ТЕКУЩЕЙ РЕАЛИЗАЦИИ ИГНОРИРУЕТСЯ
     *
     * @param userId
     * @param userProfile
     * @param descriptor
     * @throws Exception
     */
    public void insertOrUpdateProfiles2(String userId, UserProfile userProfile, String descriptor) throws Exception {

        Integer nextId = getNextProfileId();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);

            final byte[] utf8s = descriptor.getBytes("UTF8");
            int res = 0;

            if (nextId != null) {
                stmt = conn.prepareStatement("UPDATE " + DefaultProfiles.PROFILES + " SET profile=?,profileName=?,longProfile=? WHERE profileId=?");
                stmt.setNull(1, Types.VARCHAR);
                stmt.setString(2, userProfile.getProfileName());

                stmt.setAsciiStream(3, new ByteArrayInputStream(utf8s), utf8s.length);

                stmt.setInt(4, userProfile.getProfileId());
                res = stmt.executeUpdate();
            } else
                nextId = DefaultProfiles.defProfile.getProfileId();


            if (res == 0) {
                stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.PROFILES + "( uid,profileId,profileName,profile,longProfile) values(?,?,?,?,?)");
                int profileId = userProfile.getProfileId();
                if (profileId < 0) profileId = nextId;

                stmt.setString(1, String.valueOf(profileId));
                stmt.setInt(2, profileId);
                stmt.setString(3, userProfile.getProfileName());
                stmt.setNull(4, Types.VARCHAR);
                stmt.setAsciiStream(5, new ByteArrayInputStream(utf8s), utf8s.length);
                stmt.executeUpdate();
                userProfile.setProfileId(profileId);
            }
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }

    }


    /**
     * Метод БД сохранения или модификации профиля ПО ИДЕНТИФИКАТОРУ ПРОФИЛЯ. ИДЕНТИФИКАТОР ПОЛЬЗОВАТЕЛЯ В ТЕКУЩЕЙ РЕАЛИЗАЦИИ ИГНОРИРУЕТСЯ
     *
     * @param userProfile
     * @param descriptor
     * @throws Exception
     */
    public void insertOrUpdateProfiles_new(UserProfile userProfile, String descriptor) throws Exception {

        Integer nextId = getNextProfileId_new();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);

            final byte[] utf8s = descriptor.getBytes("UTF8");
            int res = 0;

            if (nextId != null) {
                stmt = conn.prepareStatement("UPDATE " + DefaultProfiles.NEW_PROFILES + " SET PROFILE_NAME=?,PROFILE_DESCRIPTOR=? WHERE PROFILEID=?");
                // stmt.setNull(1, Types.VARCHAR);
                stmt.setString(1, userProfile.getProfileName());

                stmt.setAsciiStream(2, new ByteArrayInputStream(utf8s), utf8s.length);

                stmt.setInt(3, userProfile.getProfileId());
                res = stmt.executeUpdate();
            } else
                nextId = DefaultProfiles.defProfile.getProfileId();


            if (res == 0) {
                stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.NEW_PROFILES + "( PROFILEID,PROFILE_NAME,PROFILE_DESCRIPTOR) values(?,?,?)");
                int profileId = userProfile.getProfileId();
                if (profileId < 0) profileId = nextId;

                stmt.setBigDecimal(1, new BigDecimal("" + profileId));

                stmt.setString(2, userProfile.getProfileName());

                stmt.setAsciiStream(3, new ByteArrayInputStream(utf8s), utf8s.length);
                stmt.executeUpdate();
                userProfile.setProfileId(profileId);
            }
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }

    }


    /**
     * Метод БД вставки или обновления профиля по идентификатору профиля
     *
     * @param profileId
     * @param descriptor
     * @throws Exception
     */
    private void insertOrUpdateProfileById(int profileId, String descriptor) throws Exception {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {


            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);

            stmt = conn.prepareStatement("UPDATE " + DefaultProfiles.PROFILES + " SET profile=?,longProfile=? WHERE profileId=?");
            stmt.setNull(1, Types.VARCHAR);
            final byte[] utf8s = descriptor.getBytes("UTF8");
            stmt.setAsciiStream(2, new ByteArrayInputStream(utf8s), utf8s.length);
            stmt.setInt(3, profileId);
            int res = stmt.executeUpdate();
            if (res == 0) {
                stmt = conn.prepareStatement("INSERT into " + DefaultProfiles.PROFILES + "( uid,profileId,profile,longProfile) values(?,?,?,?)");
                stmt.setNull(1, Types.VARCHAR);
                stmt.setInt(2, profileId);
                stmt.setNull(3, Types.VARCHAR);
                stmt.setAsciiStream(4, new ByteArrayInputStream(utf8s), utf8s.length);
                stmt.executeUpdate();
            }
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }

    }


    public void addProfileToUser(BigDecimal userid, BigDecimal profileId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            ps = conn.prepareStatement("select * from " + DefaultProfiles.USERS_PROFILES + " where USERID=? and PROFILEID=?");
            ps.setBigDecimal(1, userid);
            ps.setBigDecimal(2, profileId);
            rs = ps.executeQuery();

            if (!rs.next()) {
                ps = conn.prepareStatement("INSERT INTO " + DefaultProfiles.USERS_PROFILES + " (USERID,PROFILEID,MODIFIED_PROFILE) VALUES (?,?,?) ");
                ps.setBigDecimal(1, userid);
                ps.setBigDecimal(2, profileId);
                ps.setNull(3, Types.CLOB);

                ps.executeUpdate();
            }
        } finally {
            DbUtil.closeAll(rs, ps, conn, true);
        }


    }


    /**
     * Метод БД получения дескриптора профиля по идентификатору профиля
     *
     * @param profileId
     * @return
     * @throws Exception
     */
    public String getDescriptorByProfile(int profileId) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();
            String query = "SELECT profile FROM " + DefaultProfiles.PROFILES + " WHERE profileId=" + profileId;
            rs = stmt.executeQuery(query);
            if (rs.next())
                return rs.getString(1);
            return null;
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    /**
     * Метод БД получения следующиего свободного идентификатора профиля. Используется при добавлении новых записей. Допускает "дырки", так как делает добавление 1 к максимальному значению существующему в таблице профилей идентификатора профиля
     *
     * @return
     * @throws Exception
     */
    public Integer getNextProfileId() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();
            String query = "SELECT max(profileId) FROM " + DefaultProfiles.PROFILES;
            rs = stmt.executeQuery(query);
            if (rs.next())
                return rs.getInt(1) + 1;

            return null;
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }


    /**
     * Метод БД получения следующиего свободного идентификатора профиля. Используется при добавлении новых записей. Допускает "дырки", так как делает добавление 1 к максимальному значению существующему в таблице профилей идентификатора профиля
     *
     * @return
     * @throws Exception
     */
    public Integer getNextProfileId_new() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();
            String query = "SELECT max(profileId) FROM " + DefaultProfiles.NEW_PROFILES;
            rs = stmt.executeQuery(query);
            if (rs.next())
                return rs.getInt(1) + 1;

            return null;
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    /**
     * Метод БД получения дескриптора профиля по идентификатору профиля
     *
     * @param profileId
     * @return
     * @throws Exception
     */
    public String getDescriptorByProfile2(int profileId) throws Exception {
        String desc = getDescriptorByProfile(profileId);
        if (desc != null && desc.length() > 0)
            insertOrUpdateProfileById(profileId, desc);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();
            String query = "SELECT longProfile FROM " + DefaultProfiles.PROFILES + " WHERE profileId=" + profileId;
            rs = stmt.executeQuery(query);
            if (rs.next()) {

                java.sql.Clob aclob = rs.getClob(1);
                if (aclob == null)
                    return "";
                return clobToString(aclob);
            }
            return null;
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    /**
     * Метод БД получения дескриптора профиля по идентификатору профиля
     *
     * @param profileId
     * @return
     * @throws Exception
     */
    public String getDescriptorByProfile_new(BigDecimal profileId) throws Exception {


        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.prepareStatement("SELECT PROFILE_DESCRIPTOR FROM " + DefaultProfiles.NEW_PROFILES + " WHERE profileId=?");
            stmt.setBigDecimal(1, profileId);
            rs = stmt.executeQuery();
            if (rs.next()) {

                java.sql.Clob aclob = rs.getClob(1);
                if (aclob == null)
                    return "";
                return clobToString(aclob);
            }
            return null;
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    /**
     * Метод преобразования Clob в строку
     *
     * @param cl
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private String clobToString(Clob cl) throws SQLException, IOException {
        java.io.InputStream ip = null;
        ByteArrayOutputStream bos = null;

        try {
            ip = cl.getAsciiStream();

            bos = new ByteArrayOutputStream(50 * 1024);
            byte[] buff = new byte[50 * 1024];
            int c = ip.read(buff);
            while (c > 0) {
                bos.write(buff, 0, c);
                c = ip.read(buff);
            }


            return new String(bos.toByteArray(), "UTF8");
        } finally {
            if (bos != null)
                bos.close();
            if (ip != null)
                ip.close();

        }

    }

    /**
     * Метод БД проверки существования профиля по идентификатору
     *
     * @param profile
     * @return
     * @throws Exception
     */
    public boolean isProfileExist(UserProfile profile) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();
            String query = "SELECT profile FROM " + DefaultProfiles.PROFILES + " WHERE profileId=" + profile.getProfileId();
            rs = stmt.executeQuery(query);
            return rs.next();
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }


    /**
     * Метод БД проверки существования профиля по идентификатору  таблице NEW_PROFILES
     *
     * @param profile
     * @return
     * @throws Exception
     */
    public boolean isProfileExist_new(UserProfile profile) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();
            String query = "SELECT PROFILE_NAME FROM " + DefaultProfiles.NEW_PROFILES + " WHERE profileId=" + profile.getProfileId();
            rs = stmt.executeQuery(query);
            return rs.next();
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    /**
     * Метод БД удаления профиля по идентификатору
     *
     * @param profileId
     * @throws Exception
     */
    public void deleteByProfileId(int profileId) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();
            String query = "DELETE FROM " + DefaultProfiles.PROFILES + " WHERE profileId=" + profileId;
            stmt.execute(query);
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }


    /**
     * Метод БД удаления базового профиля по идентификатору
     *
     * @param profileId
     * @throws Exception
     */
    public void deleteByProfileId_new(BigDecimal profileId) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.prepareStatement("delete from " + DefaultProfiles.USERS_PROFILES + " WHERE PROFILEID=?");
            stmt.setBigDecimal(1, profileId);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("delete from " + DefaultProfiles.NEW_PROFILES + " WHERE PROFILEID=?");
            stmt.setBigDecimal(1, profileId);
            stmt.executeUpdate();
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    /**
     * Метод БД удаления базового профиля по идентификатору
     *
     * @param userId
     * @param profileId
     * @throws Exception
     */
    public void deleteProfileByProfileIdAndUserId_new(BigDecimal userId, BigDecimal profileId) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.prepareStatement("delete from " + DefaultProfiles.USERS_PROFILES + " WHERE PROFILEID=? and USERID=?");
            stmt.setBigDecimal(1, profileId);
            stmt.setBigDecimal(2, userId);
            stmt.execute();
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }


    /**
     * Метод БД получения списка профилей, привязанных к пользователю (реально сейчас идентификатор пользователя не используется и загружаются все профили)
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public List<UserProfile> getProfilesByUser(String userId) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        List<UserProfile> rv = new LinkedList<UserProfile>();

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.createStatement();
//            String query ="SELECT profileId,profileName FROM "+DefaultProfiles.PROFILES+" WHERE uid='"+userId+"'";
            String query = "SELECT profileId,profileName FROM " + DefaultProfiles.PROFILES;
            rs = stmt.executeQuery(query);
            while (rs.next())
                rv.add(new UserProfile(rs.getInt(1), rs.getString(2)));

            return rv;
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }


    /**
     * Метод БД получения списка профилей, привязанных к пользователю (новая версия)
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public List<UserProfile> getProfilesByUser_new(BigDecimal userId, boolean complete) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<UserProfile> rv = new LinkedList<UserProfile>();


        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            String select = "SELECT profileId,PROFILE_NAME" + (complete ? ",PROFILE_DESCRIPTOR " : " ") + "FROM " + DefaultProfiles.NEW_PROFILES + " where PROFILEID in (select PROFILEID from USERS_PROFILES where USERID=?)";
            System.out.println(select);
            stmt = conn.prepareStatement(select);

//            String query ="SELECT profileId,profileName FROM "+DefaultProfiles.PROFILES+" WHERE uid='"+userId+"'";
            stmt.setBigDecimal(1, userId);
            rs = stmt.executeQuery();

            while (rs.next())


            {
                if (complete) {

                    java.sql.Clob aclob = rs.getClob(3);
                    String profiledescriptor = "";
                    if (aclob != null)

                        profiledescriptor = clobToString(aclob);
                    rv.add(new UserProfile(rs.getBigDecimal(1).intValue(), rs.getString(2), profiledescriptor));
                } else
                    rv.add(new UserProfile(rs.getBigDecimal(1).intValue(), rs.getString(2)));
            }

            return rv;

        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }


    }

    /**
     * Метод БД проверки существования указанного поля в указанной таблице
     *
     * @param conn
     * @param tableNamePattern
     * @param colName
     * @return
     * @throws SQLException
     */

    public boolean isColumnExist(Connection conn, String tableNamePattern, String colName) throws SQLException {
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getColumns(null, "APP", tableNamePattern, colName);
            return rs.next();
        } finally {
            DbUtil.closeAll(rs, null, null, true);
        }
    }

    public boolean isExists(Connection conn, String tableNamePattern) throws SQLException {
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getTables(null, "APP", tableNamePattern, new String[]{"TABLE", "VIEW"});
            return rs.next();
        } finally {
            DbUtil.closeAll(rs, null, null, true);
        }
    }

  /*  public static void main(String[] args) throws Exception {
        TestUserProfiles tst = new TestUserProfiles();

        List<UserProfile> res1 = tst.getProfilesByUser(DefaultProfiles.DEF_USER_ID);

        String res = tst.getDescriptorByProfile(0);
    }*/

    /**
     * Метод БД получения настроек пользователя по идентификатору пользователя
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public String getSettingsByUserId_new(BigDecimal userId) throws Exception {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.prepareStatement("SELECT USERSETTINGS FROM " + DefaultProfiles.USERS + " WHERE USERID=?");
            stmt.setBigDecimal(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {

                java.sql.Clob aclob = rs.getClob(1);
                if (aclob == null)
                    return "";
                return clobToString(aclob);
            }
            return null;
        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }


    public void insertOrUpdateUser(BigDecimal userId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
            stmt = conn.prepareStatement("SELECT USERID FROM " + DefaultProfiles.USERS + " WHERE USERID=?");
            stmt.setBigDecimal(1, userId);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                stmt = conn.prepareStatement("INSERT into USERS (USERID,USERSETTINGS) VALUES (?,?)");
                stmt.setBigDecimal(1, userId);
                stmt.setNull(2, Types.CLOB);

                stmt.executeUpdate();

            }

        } finally {
            DbUtil.closeAll(rs, stmt, conn, true);
        }


    }


    public static void main(String[] args) {
        try {
            TestUserProfiles tup = new TestUserProfiles();

            tup.copyExistingProfiles();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
