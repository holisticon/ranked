
    create table association_value_entry (
        id bigint not null,
        association_key varchar(255) not null,
        association_value varchar(255),
        saga_id varchar(255) not null,
        saga_type varchar(255),
        primary key (id)
    );

    create table domain_event_entry (
        global_index bigint not null,
        event_identifier varchar(255) not null,
        meta_data longblob,
        payload longblob not null,
        payload_revision varchar(255),
        payload_type varchar(255) not null,
        time_stamp varchar(255) not null,
        aggregate_identifier varchar(255) not null,
        sequence_number bigint not null,
        type varchar(255),
        primary key (global_index)
    );

    create table hibernate_sequence (
        next_val bigint
    );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    create table saga_entry (
        saga_id varchar(255) not null,
        revision varchar(255),
        saga_type varchar(255),
        serialized_saga longblob,
        primary key (saga_id)
    );

    create table snapshot_event_entry (
        aggregate_identifier varchar(255) not null,
        sequence_number bigint not null,
        type varchar(255) not null,
        event_identifier varchar(255) not null,
        meta_data longblob,
        payload longblob not null,
        payload_revision varchar(255),
        payload_type varchar(255) not null,
        time_stamp varchar(255) not null,
        primary key (aggregate_identifier, sequence_number, type)
    );
create index IDXk45eqnxkgd8hpdn6xixn8sgft on association_value_entry (saga_type, association_key, association_value);
create index IDXgv5k1v2mh6frxuy5c0hgbau94 on association_value_entry (saga_id, saga_type);

    alter table domain_event_entry
        add constraint UK8s1f994p4la2ipb13me2xqm1w unique (aggregate_identifier, sequence_number);

    alter table domain_event_entry
        add constraint UK_fwe6lsa8bfo6hyas6ud3m8c7x unique (event_identifier);

    alter table snapshot_event_entry
        add constraint UK_e1uucjseo68gopmnd0vgdl44h unique (event_identifier);
