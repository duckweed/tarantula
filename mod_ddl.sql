renameDuplicatedForeignKeys(['fk_account_id', 'account(id)', 'account_protocol', 'billing_detail'])
renameDuplicatedForeignKeys(['fk_billing_detail_id', 'billing_detail(id)', 'bank_account', 'credit_card'])
renameDuplicatedForeignKeys(['fk_campaign_id', 'campaign(id)', 'campaign_dimension_state', 'campaign_to_campaign_auto_approval_category', 'pixel_campaign_mapping', 'targeting'])
renameDuplicatedForeignKeys(['fk_filter_id', 'publisher_auto_approval_filter(id)', 'publisher_auto_approval_filter_blacklist', 'publisher_filter_to_website'])
renameDuplicatedForeignKeys(['fk_targeting_id', 'targeting(id)', 'campaign_category', 'campaign_demographic_age', 'campaign_demographic_gender', 'campaign_demographic_income'])




private void renameDuplicatedForeignKeys(def list) {
    String key = list[0]
    String references = list[1]
    String[] tables = list.subList(2, list.size())
    tables.each {
        println "alter table $it drop constraint $key;"
        println "alter table $it add constraint ${key + '_' + it} foreign key ($key) references $references;"
    }
}

alter view v_campaign_performance drop column "cch-ctr(%)";

alter table db_version rename column version to vignette;
alter table stats_file_ctr rename column version to vignette;


alter table account_protocol drop constraint fk_account_id;
alter table account_protocol add constraint fk_account_id_account_protocol foreign key (fk_account_id) references account(id);
alter table billing_detail drop constraint fk_account_id;
alter table billing_detail add constraint fk_account_id_billing_detail foreign key (fk_account_id) references account(id);
alter table bank_account drop constraint fk_billing_detail_id;
alter table bank_account add constraint fk_billing_detail_id_bank_account foreign key (fk_billing_detail_id) references billing_detail(id);
alter table credit_card drop constraint fk_billing_detail_id;
alter table credit_card add constraint fk_billing_detail_id_credit_card foreign key (fk_billing_detail_id) references billing_detail(id);
alter table campaign_dimension_state drop constraint fk_campaign_id;
alter table campaign_dimension_state add constraint fk_campaign_id_campaign_dimension_state foreign key (fk_campaign_id) references campaign(id);
alter table campaign_to_campaign_auto_approval_category drop constraint fk_campaign_id;
alter table campaign_to_campaign_auto_approval_category add constraint fk_campaign_id_campaign_to_campaign_auto_approval_category foreign key (fk_campaign_id) references campaign(id);
alter table pixel_campaign_mapping drop constraint fk_campaign_id;
alter table pixel_campaign_mapping add constraint fk_campaign_id_pixel_campaign_mapping foreign key (fk_campaign_id) references campaign(id);
alter table targeting drop constraint fk_campaign_id;
alter table targeting add constraint fk_campaign_id_targeting foreign key (fk_campaign_id) references campaign(id);
alter table publisher_auto_approval_filter_blacklist drop constraint fk_filter_id;
alter table publisher_auto_approval_filter_blacklist add constraint fk_filter_id_publisher_auto_approval_filter_blacklist foreign key (fk_filter_id) references publisher_auto_approval_filter(id);
alter table publisher_filter_to_website drop constraint fk_filter_id;
alter table publisher_filter_to_website add constraint fk_filter_id_publisher_filter_to_website foreign key (fk_filter_id) references publisher_auto_approval_filter(id);
alter table campaign_category drop constraint fk_targeting_id;
alter table campaign_category add constraint fk_targeting_id_campaign_category foreign key (fk_targeting_id) references targeting(id);
alter table campaign_demographic_age drop constraint fk_targeting_id;
alter table campaign_demographic_age add constraint fk_targeting_id_campaign_demographic_age foreign key (fk_targeting_id) references targeting(id);
alter table campaign_demographic_gender drop constraint fk_targeting_id;
alter table campaign_demographic_gender add constraint fk_targeting_id_campaign_demographic_gender foreign key (fk_targeting_id) references targeting(id);
alter table campaign_demographic_income drop constraint fk_targeting_id;
alter table campaign_demographic_income add constraint fk_targeting_id_campaign_demographic_income foreign key (fk_targeting_id) references targeting(id);
