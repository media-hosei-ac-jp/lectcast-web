CREATE TABLE IF NOT EXISTS `lti_keypair`
(
    `lti_key`     VARCHAR(64) NOT NULL,
    `lti_secret`  VARCHAR(64) NOT NULL,
    `description` VARCHAR(64) NULL,
    PRIMARY KEY (`lti_key`)
);

CREATE TABLE IF NOT EXISTS `channel`
(
    `id`                   VARCHAR(36) NOT NULL,
    `lti_context_id`       VARCHAR(64) NOT NULL,
    `lti_resource_link_id` VARCHAR(64) NOT NULL,
    `title`                VARCHAR(64) NOT NULL,
    `author`               VARCHAR(64) NULL,
    `description`          TEXT(2048)  NULL,
    `explicit`             TINYINT(1)  NOT NULL DEFAULT 0,
    `image`                VARCHAR(64) NULL,
    `language`             VARCHAR(8)  NULL,
    `created_at`           DATETIME    NOT NULL,
    `updated_at`           DATETIME    NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `key_context_id_resource_link_id` (`lti_context_id` ASC, `lti_resource_link_id` ASC) VISIBLE
);

CREATE TABLE IF NOT EXISTS `item`
(
    `id`          VARCHAR(36)  NOT NULL,
    `channel_id`  VARCHAR(36)  NOT NULL,
    `s3_key`      VARCHAR(64)  NOT NULL,
    `title`       VARCHAR(64)  NOT NULL,
    `description` TEXT(2048)   NULL,
    `duration`    INT UNSIGNED NOT NULL,
    `explicit`    TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`  DATETIME     NOT NULL,
    `updated_at`  DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_items_channels_idx` (`channel_id` ASC) VISIBLE,
    UNIQUE INDEX `key_s3_key` (`s3_key` ASC) VISIBLE,
    CONSTRAINT `fk_items_channels`
        FOREIGN KEY (`channel_id`)
            REFERENCES `channel` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS `feed`
(
    `id`          VARCHAR(36) NOT NULL,
    `channel_id`  VARCHAR(36) NOT NULL,
    `lti_user_id` VARCHAR(64) NOT NULL,
    `active`      TINYINT(1)  NOT NULL DEFAULT 1,
    `created_at`  DATETIME    NOT NULL,
    `updated_at`  DATETIME    NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_feed_channel_idx` (`channel_id` ASC) VISIBLE,
    CONSTRAINT `fk_feed_channel`
        FOREIGN KEY (`channel_id`)
            REFERENCES `channel` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);
