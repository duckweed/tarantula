

renameDuplicatedForeignKeys(['fk_account_id',           'account(id)',                          'account_protocol', 'billing_detail'])
renameDuplicatedForeignKeys(['fk_billing_detail_id',    'billing_detail(id)',                   'bank_account', 'credit_card'])
renameDuplicatedForeignKeys(['fk_campaign_id',          'campaign(id)',                         'campaign_dimension_state', 'campaign_to_campaign_auto_approval_category', 'pixel_campaign_mapping', 'targeting'])
renameDuplicatedForeignKeys(['fk_filter_id',            'publisher_auto_approval_filter(id)',   'publisher_auto_approval_filter_blacklist', 'publisher_filter_to_website'])
renameDuplicatedForeignKeys(['fk_targeting_id',         'targeting(id)',                        'campaign_category', 'campaign_demographic_age', 'campaign_demographic_gender', 'campaign_demographic_income'])




private void renameDuplicatedForeignKeys(def list) {
    String key = list[0]
    String references = list[1]
    String[] tables = list.subList(2, list.size())
    tables.each {
        println "alter table $it drop constraint $key;"
        println "alter table $it add constraint ${key + '_' + it} foreign key ($key) references $references;"
    }
}
