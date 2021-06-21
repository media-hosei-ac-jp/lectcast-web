CREATE TABLE IF NOT EXISTS `lti_keypair` (
  `lti_key` VARCHAR(64) NOT NULL,
  `lti_secret` VARCHAR(64) NOT NULL,
  `description` VARCHAR(64) NULL,
  PRIMARY KEY (`lti_key`))
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `channel` (
  `id` VARCHAR(36) NOT NULL,
  `lti_context_id` VARCHAR(64) NOT NULL,
  `lti_resource_link_id` VARCHAR(64) NOT NULL,
  `title` VARCHAR(64) NOT NULL,
  `author` VARCHAR(64) NULL,
  `description` TEXT(2048) NULL,
  `explicit` TINYINT(1) NOT NULL DEFAULT 0,
  `image` VARCHAR(64) NULL,
  `language` VARCHAR(8) NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_context_id_resource_link_id` (`lti_context_id` ASC, `lti_resource_link_id` ASC) )
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `item` (
  `id` VARCHAR(36) NOT NULL,
  `channel_id` VARCHAR(36) NOT NULL,
  `filename` VARCHAR(64) NOT NULL DEFAULT '',
  `s3_key` VARCHAR(64) NOT NULL,
  `title` VARCHAR(64) NOT NULL,
  `description` TEXT(2048) NULL,
  `duration` INT UNSIGNED NOT NULL,
  `explicit` TINYINT(1) NOT NULL DEFAULT 0,
  `date_from` DATETIME NOT NULL DEFAULT '0001-01-03T00:00:00',
  `date_to` DATETIME NOT NULL DEFAULT '9999-12-31T23:59:59',
  `is_infinity` TINYINT(1) NOT NULL DEFAULT 1,
  `is_converted` TINYINT(1) NOT NULL DEFAULT 0,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_items_channels_idx` (`channel_id` ASC) ,
  UNIQUE INDEX `key_s3_key` (`s3_key` ASC) ,
  CONSTRAINT `fk_items_channels`
    FOREIGN KEY (`channel_id`)
    REFERENCES `channel` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `feed` (
  `id` VARCHAR(36) NOT NULL,
  `channel_id` VARCHAR(36) NOT NULL,
  `lti_user_id` VARCHAR(64) NOT NULL,
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_feed_channel_idx` (`channel_id` ASC) ,
  CONSTRAINT `fk_feed_channel`
    FOREIGN KEY (`channel_id`)
    REFERENCES `channel` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;
