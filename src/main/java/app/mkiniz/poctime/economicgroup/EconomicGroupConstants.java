package app.mkiniz.poctime.economicgroup;

public class EconomicGroupConstants {
    public static final String NAME_NOT_BLANK = "economicgroup.name.not.blank";
    public static final String ORGANIZATIONS_NOT_FOUND = "economicgroup.organizations.not.found|%s";
    public static final String ECONOMIC_GROUP_ALREADY_EXISTS = "economicgroup.already.exists";
    public static final String ECONOMIC_GROUP_NOT_FOUND = "economicgroup.not.found";
    public static final String REMOVE_ORGANIZATION_IDS_NOT_NULL = "economicgroup.remove.organization.ids.not.null";
    public static final String REMOVE_ORGANIZATION_IDS_NOT_EMPTY = "economicgroup.remove.organization.ids.not.empty";

    // RABBITMQ CONSTANTS
    public static final String ECONOMIC_GROUP_IN_EXCHANGE = "exchange.in.economicgroup";
    public static final String ECONOMIC_GROUP_OUT_EXCHANGE = "exchange.out.economicgroup";
}
