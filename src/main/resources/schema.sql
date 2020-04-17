CREATE TABLE IF NOT EXISTS `lti_key` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `key` VARCHAR(64) NOT NULL,
  `secret` VARCHAR(64) NOT NULL,
  `description` VARCHAR(64) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_UNIQUE` (`key` ASC) VISIBLE)
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `channel` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `context_id` VARCHAR(64) NOT NULL,
  `resource_link_id` VARCHAR(64) NOT NULL,
  `title` VARCHAR(64) NOT NULL DEFAULT "",
  `author` VARCHAR(64) DEFAULT NULL,
  `description` TEXT(2048) DEFAULT NULL,
  `explicit` TINYINT(1) NOT NULL DEFAULT 0,
  `image` VARCHAR(64) DEFAULT NULL,
  `language` VARCHAR(8) DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_context_id_resource_link_id` (`context_id` ASC, `resource_link_id` ASC) VISIBLE)
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `channel_id` INT NOT NULL,
  `guid` VARCHAR(64) DEFAULT NULL,
  `s3_key` VARCHAR(64) NOT NULL DEFAULT "",
  `title` VARCHAR(64) NOT NULL DEFAULT "",
  `description` TEXT(2048) DEFAULT NULL,
  `duration` INT UNSIGNED NOT NULL DEFAULT 0,
  `explicit` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX (`s3_key`),
  INDEX `fk_items_channels_idx` (`channel_id` ASC) VISIBLE,
  CONSTRAINT `fk_items_channels`
    FOREIGN KEY (`channel_id`)
    REFERENCES `channel` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;
