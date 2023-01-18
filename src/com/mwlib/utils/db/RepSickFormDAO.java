/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mwlib.utils.db;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * @author STsimbalov
 */
public final class RepSickFormDAO {

    public static final String GET_REP_SICK_FORM = "{? = call PKG_REP_SICK_FORM.get_rep_sickform_by_mv(?)}";
    //public static final String GET_REP_SICK_FORM = "{? = call PKG_REP_SICK_FORM.get_rep_sickform(?)}";
    public static final String REP_IN_SICK_FORM_BY_PERIOD="{? = call PKG_REP_SICK_FORM.rep_in_sick_form_by_period(?)}";
    public static final String REP_OUT_SICK_FORM_BY_PERIOD="{? = call PKG_REP_SICK_FORM.rep_out_sick_form_by_period(?)}";
    public static final String REP_OUT_RO_SICK_FORM_BY_PERIOD="{? = call PKG_REP_SICK_FORM.rep_out_ro_sick_form_by_period(?)}";

    public static final String REP_R1_SICK_FORM_BY_PERIOD="{? = call PKG_REP_SICK_FORM.rep_r1_sick_form_by_period(?)}";
    public static final String REP_R2_SICK_FORM_BY_PERIOD="{? = call PKG_REP_SICK_FORM.rep_r2_sick_form_by_period(?)}";

    public static final String REP_DEL_SICK_FORM_BY_PERIOD="{? = call PKG_REP_SICK_FORM.rep_del_sick_form_by_period(?)}";

    public static final String REP_SPLIT_BY_RO_WRO="{? = call PKG_REP_SICK_FORM.rep_split_by_ro_wro(?)}";

    public static final String REP_SPLIT_DNUM_BY_WRO_ORG="{? = call PKG_REP_SICK_FORM.rep_split_dnum_by_wro_org(?)}";

    public static final String GET_REP_SICK_FORM_BY_MEDRQ="{? = call PKG_REP_SICK_FORM.get_rep_sickform_by_medrq(?)}";

    public static final String REP_OMEDRQ_SICK_FORM_BY_PERIOD="{? = call PKG_REP_SICK_FORM.rep_omedrq_sick_form_by_period(?)}";

    public static final String FIND_SICK_FORM_BY_PER_NUM_SER ="{? = call PKG_REP_SICK_FORM.find_sick_form_by_per_num_ser(?)}";

    public static final String FIND_LOST_SICK_FORM_BY_NUM="SELECT FRST_NUM,LAST_NUM,br.name || '(' || ROCODE || ')' as RO_NAME,NAMEMO,YEAR,MONTH,TYPELOST,\n" +
                    "CASE WHEN TYPELOST=0\n" +
                    "THEN 'Недействительные'\n" +
                    "WHEN TYPELOST=1\n" +
                    "THEN 'Утерянные(похищенные)'\n" +
                    "WHEN TYPELOST=2\n" +
                    "THEN 'Найденные'\n" +
                    "END as TYPELOSTSTAT,QUANT from simp_mblost lst,cprt_branch br \n" +
                    "where  lst.ROCODE*100=br.id(+) and lst.FRST_FND <= ?  and ? <= lst.LAST_FND";


    public static final String FIND_LOST_SICK_FORM_BY_LIKE_NUM="SELECT FRST_NUM,LAST_NUM,br.name || '(' || ROCODE || ')' as RO_NAME,NAMEMO,YEAR,MONTH,TYPELOST,\n" +
                    "CASE WHEN TYPELOST=0\n" +
                    "THEN 'Недействительные'\n" +
                    "WHEN TYPELOST=1\n" +
                    "THEN 'Утерянные(похищенные)'\n" +
                    "WHEN TYPELOST=2\n" +
                    "THEN 'Найденные'\n" +
                    "END as TYPELOSTSTAT,QUANT from simp_mblost lst,cprt_branch br \n" +
                    "where  lst.ROCODE*100=br.id(+) and (FRST_NUM like ?  or LAST_FND like ?)";
    //
    private static RepSickFormDAO instance = new RepSickFormDAO();

    //
    public static RepSickFormDAO getInstance() {
        return instance;
    }


    private String _findLostSickForm(String nnum,String mode)
    {
        StringBuilder result = new StringBuilder();
        long stTime = Calendar.getInstance().getTime().getTime();
        Connection conn = null;
        PreparedStatement cs = null;
        ResultSet rs = null;
        try {
            conn = DbUtil.getJNDIConnection(DbUtil.DS_ORA_NAME);
            if (mode==null || "".equals(mode) || "0".equals(mode) || "false".equalsIgnoreCase(mode))
            {
                cs = conn.prepareStatement(FIND_LOST_SICK_FORM_BY_NUM);
                int num = Integer.parseInt(nnum);
                cs.setInt(1,num);
                cs.setInt(2,num);
            }
            else
            {
                cs = conn.prepareStatement(FIND_LOST_SICK_FORM_BY_LIKE_NUM);
                String reqnum=nnum.replaceAll("\\*","%");
                if (!reqnum.contains("%"))
                        reqnum=reqnum+"%";
                cs.setString(1, reqnum);
                cs.setString(2, reqnum);
            }
            rs = cs.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int colc = metaData.getColumnCount();
            List<String> colnames=new LinkedList<String>();
            for (int i=1;i<=colc;i++)
                colnames.add(metaData.getColumnName(i));
            int i=0;
            while(rs.next())
            {
                result.append("<row id='").append(i).append("'>");
                for (String colname : colnames) {
                    result.append("<").append(colname).append(">").append(rs.getString(colname)).append("</").append(colname).append(">");
                }
                result.append("</row>");
                i++;
            }
            result.insert(0, "<?xml version=\"1.0\" encoding=\"windows-1251\"?><rows  total='" + i + "'>").append("</rows>");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(rs, cs, conn, false);
        }
//        LogFactory.log.info((Calendar.getInstance().getTime().getTime() - stTime) + "ms");
        return result.toString();
    }



//    public String _repSickForm(final PageFilter filter,String getRepSickForm) throws Exception {
//        //
//        long stTime = Calendar.getInstance().getTime().getTime();
//        System.out.println("filter.toXML=" + filter.toXML());
//        String result = null;
//        CallableStatement cs = null;
//        Connection conn = null;
//        Clob clob;
//        try {
//            conn = DbUtil.getJNDIConnection();
//
//            cs = conn.prepareCall(getRepSickForm);
//            cs.registerOutParameter(1, Types.CLOB);
//            cs.setString(2, filter.toXML());
//            cs.execute();
//            clob = cs.getClob(1);
//            if (clob != null)
//                result = clob.getSubString(1, (int) clob.length());
//            else
//                return null;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            DbUtil.closeAll(null, cs, conn);
//        }
//        LogFactory.log.info((Calendar.getInstance().getTime().getTime() - stTime) + "ms");
//        return result;
//    }


    private static String IBSDS_NAME = "jdbc/fssibs";
    private static DataSource ds;
    static
    {
        try {
            DataSource ds_l = null;
            InitialContext initialContext = null;
            try {
                initialContext = new InitialContext();
            } catch (NamingException e) {
                e.printStackTrace();
            }

            if (initialContext!=null)
            {
                try {
                    ds_l = (DataSource) initialContext.lookup(IBSDS_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (ds_l==null)
                    {
                        Context envCtx = (Context) initialContext.lookup("java:comp/env");
                        ds_l = (DataSource) envCtx.lookup(IBSDS_NAME);
                    }
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }

            if (ds_l!=null)
              ds = ds_l;
        } catch (Exception e) {
            //
        }
    }


    private static final String f_check_ln_number ="{? = call PCK_CHECK.f_check_ln_number(?)}";

    /**
     * Проверить номер ЛН по базе IBS
     * @param number - номер ЛН
     * @return - номер найден/не найден/ == null - произошла ошибка при проверки
     */
//    public static Boolean checkLnNumberByIBSDb(String number)
//    {
//        CallableStatement cs = null;
//        Connection conn      = null;
//        try{
//            if (ds!=null)
//            {
//                conn = ds.getConnection();
//                cs   = conn.prepareCall(f_check_ln_number);
//                cs.registerOutParameter(1, OracleTypes.NUMBER);
//                cs.setString(2, number);
//                cs.execute();
//                return cs.getInt(1)==0;
//            }
//            else
//                throw new SQLException("Can't connect to LnChecker dataSource: "+IBSDS_NAME);
//        }
//        catch(SQLException e){
//            e.printStackTrace();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//        finally{
//            DbUtil.closeAll(null, cs, conn, false);
//        }
//        return null;
//    }




}
