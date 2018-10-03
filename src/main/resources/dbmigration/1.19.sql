-- apply changes
create table rc_skills_bindings (
  id                            integer auto_increment not null,
  owner_id                      integer not null,
  item                          varchar(32) not null,
  skill                         varchar(32),
  args                          varchar(32),
  constraint pk_rc_skills_bindings primary key (id)
);

create table rc_skills_data_alias (
  name                          varchar(255) not null,
  description                   TEXT,
  parent                        varchar(255),
  skill                         varchar(255),
  hidden                        tinyint(1) default 0 not null,
  constraint pk_rc_skills_data_alias primary key (name)
);

create table rc_skills_data_profession (
  name                          varchar(255) not null,
  filename                      varchar(255),
  description                   TEXT,
  max_level                     integer not null,
  formula                       varchar(255),
  parent                        varchar(255),
  skills                        TEXT,
  type                          varchar(255),
  constraint pk_rc_skills_data_profession primary key (name)
);

create table rc_skills_heroes (
  id                            integer auto_increment not null,
  player                        varchar(255),
  player_id                     varchar(40) not null,
  selected_profession           varchar(255),
  exp                           integer not null,
  level                         integer not null,
  health                        double not null,
  exp_pool_id                   integer,
  constraint uq_rc_skills_heroes_player_id unique (player_id),
  constraint uq_rc_skills_heroes_exp_pool_id unique (exp_pool_id),
  constraint pk_rc_skills_heroes primary key (id)
);

create table rc_skills_attributes (
  id                            integer auto_increment not null,
  hero_id                       integer,
  attribute                     varchar(29),
  base_value                    integer not null,
  current_value                 integer not null,
  constraint ck_rc_skills_attributes_attribute check ( attribute in ('STRENGTH','AGILITY','STAMINA','INTELLECT','SPIRIT','CRITICAL_STRIKE_RATING','HIT_RATING','MAGICAL_HIT','ATTACK_POWER','SPELL_POWER','DODGE_RATING','PARRY_RATING','SHIELD_BLOCK_RATING','DEFENSE_RATING','HEAL','HASTE_RATING','ARMOR_PENETRATION','WEAPON_SKILL_RATING','RANGED_CRITICAL_STRIKE_RATING','EXPERTISE_RATING_2','EXPERTISE_RATING','RESILIENCE_RATING','RANGED_ATTACK_POWER','MANA_REGENERATION','ARMOR_PENETRATION_RATING','HEALTH_REGEN','SPELL_PENETRATION','BLOCK_VALUE','MASTERY_RATING','FIRE_RESISTANCE','FROST_RESISTANCE','HOLY_RESISTANCE','SHADOW_RESISTANCE','NATURE_RESISTANCE','ARCANE_RESISTANCE')),
  constraint pk_rc_skills_attributes primary key (id)
);

create table rc_skills_exp_pool (
  id                            integer auto_increment not null,
  player                        varchar(255),
  hero_id                       integer not null,
  player_id                     varchar(40),
  exp                           integer not null,
  constraint pk_rc_skills_exp_pool primary key (id)
);

create table rc_skills_hero_options (
  id                            integer auto_increment not null,
  hero_id                       integer,
  option_key                    varchar(255),
  option_value                  varchar(255),
  constraint pk_rc_skills_hero_options primary key (id)
);

create table rc_skills_hero_professions (
  id                            integer auto_increment not null,
  name                          varchar(255) not null,
  hero_id                       integer,
  level                         integer not null,
  exp                           integer not null,
  active                        tinyint(1) default 0 not null,
  constraint pk_rc_skills_hero_professions primary key (id)
);

create table rc_skills_hero_resources (
  id                            integer auto_increment not null,
  name                          varchar(255) not null,
  value                         double not null,
  profession_id                 integer,
  constraint pk_rc_skills_hero_resources primary key (id)
);

create table rc_skills_hero_skills (
  id                            integer auto_increment not null,
  name                          varchar(255) not null,
  profession_id                 integer,
  hero_id                       integer,
  level                         integer not null,
  exp                           integer not null,
  last_cast                     datetime(6),
  unlocked                      tinyint(1) default 0 not null,
  unlock_time                   datetime(6),
  cast_count                    integer not null,
  constraint pk_rc_skills_hero_skills primary key (id)
);

create table rc_language (
  code                          char(1) not null,
  name                          varchar(32) not null,
  constraint pk_rc_language primary key (code)
);

create table rc_skills_profession (
  name_key                      varchar(32) not null,
  parent_name_key               varchar(32),
  max_level                     integer not null,
  type                          varchar(255),
  constraint pk_rc_skills_profession primary key (name_key)
);

create table rc_runestones (
  id                            integer auto_increment not null,
  custom_item_id                integer not null,
  max_uses                      integer not null,
  remaining_uses                integer not null,
  world                         varchar(255),
  x                             double not null,
  y                             double not null,
  z                             double not null,
  yaw                           float not null,
  pitch                         float not null,
  last_update                   datetime(6) not null,
  constraint pk_rc_runestones primary key (id)
);

create table rc_skills_skill (
  name_key                      varchar(32) not null,
  profession_name_key           varchar(32) not null,
  cretimestamp                  datetime(6),
  updtimestamp                  datetime(6),
  enabled                       tinyint(1) default 0 not null,
  hidden                        tinyint(1) default 0 not null,
  icon_material                 varchar(28),
  req_level                     integer not null,
  max_level                     integer not null,
  cooldown                      integer not null,
  reach                         integer not null,
  constraint ck_rc_skills_skill_icon_material check ( icon_material in ('AIR','STONE','GRASS','DIRT','COBBLESTONE','WOOD','SAPLING','BEDROCK','WATER','STATIONARY_WATER','LAVA','STATIONARY_LAVA','SAND','GRAVEL','GOLD_ORE','IRON_ORE','COAL_ORE','LOG','LEAVES','SPONGE','GLASS','LAPIS_ORE','LAPIS_BLOCK','DISPENSER','SANDSTONE','NOTE_BLOCK','BED_BLOCK','POWERED_RAIL','DETECTOR_RAIL','PISTON_STICKY_BASE','WEB','LONG_GRASS','DEAD_BUSH','PISTON_BASE','PISTON_EXTENSION','WOOL','PISTON_MOVING_PIECE','YELLOW_FLOWER','RED_ROSE','BROWN_MUSHROOM','RED_MUSHROOM','GOLD_BLOCK','IRON_BLOCK','DOUBLE_STEP','STEP','BRICK','TNT','BOOKSHELF','MOSSY_COBBLESTONE','OBSIDIAN','TORCH','FIRE','MOB_SPAWNER','WOOD_STAIRS','CHEST','REDSTONE_WIRE','DIAMOND_ORE','DIAMOND_BLOCK','WORKBENCH','CROPS','SOIL','FURNACE','BURNING_FURNACE','SIGN_POST','WOODEN_DOOR','LADDER','RAILS','COBBLESTONE_STAIRS','WALL_SIGN','LEVER','STONE_PLATE','IRON_DOOR_BLOCK','WOOD_PLATE','REDSTONE_ORE','GLOWING_REDSTONE_ORE','REDSTONE_TORCH_OFF','REDSTONE_TORCH_ON','STONE_BUTTON','SNOW','ICE','SNOW_BLOCK','CACTUS','CLAY','SUGAR_CANE_BLOCK','JUKEBOX','FENCE','PUMPKIN','NETHERRACK','SOUL_SAND','GLOWSTONE','PORTAL','JACK_O_LANTERN','CAKE_BLOCK','DIODE_BLOCK_OFF','DIODE_BLOCK_ON','STAINED_GLASS','TRAP_DOOR','MONSTER_EGGS','SMOOTH_BRICK','HUGE_MUSHROOM_1','HUGE_MUSHROOM_2','IRON_FENCE','THIN_GLASS','MELON_BLOCK','PUMPKIN_STEM','MELON_STEM','VINE','FENCE_GATE','BRICK_STAIRS','SMOOTH_STAIRS','MYCEL','WATER_LILY','NETHER_BRICK','NETHER_FENCE','NETHER_BRICK_STAIRS','NETHER_WARTS','ENCHANTMENT_TABLE','BREWING_STAND','CAULDRON','ENDER_PORTAL','ENDER_PORTAL_FRAME','ENDER_STONE','DRAGON_EGG','REDSTONE_LAMP_OFF','REDSTONE_LAMP_ON','WOOD_DOUBLE_STEP','WOOD_STEP','COCOA','SANDSTONE_STAIRS','EMERALD_ORE','ENDER_CHEST','TRIPWIRE_HOOK','TRIPWIRE','EMERALD_BLOCK','SPRUCE_WOOD_STAIRS','BIRCH_WOOD_STAIRS','JUNGLE_WOOD_STAIRS','COMMAND','BEACON','COBBLE_WALL','FLOWER_POT','CARROT','POTATO','WOOD_BUTTON','SKULL','ANVIL','TRAPPED_CHEST','GOLD_PLATE','IRON_PLATE','REDSTONE_COMPARATOR_OFF','REDSTONE_COMPARATOR_ON','DAYLIGHT_DETECTOR','REDSTONE_BLOCK','QUARTZ_ORE','HOPPER','QUARTZ_BLOCK','QUARTZ_STAIRS','ACTIVATOR_RAIL','DROPPER','STAINED_CLAY','STAINED_GLASS_PANE','LEAVES_2','LOG_2','ACACIA_STAIRS','DARK_OAK_STAIRS','SLIME_BLOCK','BARRIER','IRON_TRAPDOOR','PRISMARINE','SEA_LANTERN','HAY_BLOCK','CARPET','HARD_CLAY','COAL_BLOCK','PACKED_ICE','DOUBLE_PLANT','STANDING_BANNER','WALL_BANNER','DAYLIGHT_DETECTOR_INVERTED','RED_SANDSTONE','RED_SANDSTONE_STAIRS','DOUBLE_STONE_SLAB2','STONE_SLAB2','SPRUCE_FENCE_GATE','BIRCH_FENCE_GATE','JUNGLE_FENCE_GATE','DARK_OAK_FENCE_GATE','ACACIA_FENCE_GATE','SPRUCE_FENCE','BIRCH_FENCE','JUNGLE_FENCE','DARK_OAK_FENCE','ACACIA_FENCE','SPRUCE_DOOR','BIRCH_DOOR','JUNGLE_DOOR','ACACIA_DOOR','DARK_OAK_DOOR','END_ROD','CHORUS_PLANT','CHORUS_FLOWER','PURPUR_BLOCK','PURPUR_PILLAR','PURPUR_STAIRS','PURPUR_DOUBLE_SLAB','PURPUR_SLAB','END_BRICKS','BEETROOT_BLOCK','GRASS_PATH','END_GATEWAY','COMMAND_REPEATING','COMMAND_CHAIN','FROSTED_ICE','MAGMA','NETHER_WART_BLOCK','RED_NETHER_BRICK','BONE_BLOCK','STRUCTURE_VOID','OBSERVER','WHITE_SHULKER_BOX','ORANGE_SHULKER_BOX','MAGENTA_SHULKER_BOX','LIGHT_BLUE_SHULKER_BOX','YELLOW_SHULKER_BOX','LIME_SHULKER_BOX','PINK_SHULKER_BOX','GRAY_SHULKER_BOX','SILVER_SHULKER_BOX','CYAN_SHULKER_BOX','PURPLE_SHULKER_BOX','BLUE_SHULKER_BOX','BROWN_SHULKER_BOX','GREEN_SHULKER_BOX','RED_SHULKER_BOX','BLACK_SHULKER_BOX','WHITE_GLAZED_TERRACOTTA','ORANGE_GLAZED_TERRACOTTA','MAGENTA_GLAZED_TERRACOTTA','LIGHT_BLUE_GLAZED_TERRACOTTA','YELLOW_GLAZED_TERRACOTTA','LIME_GLAZED_TERRACOTTA','PINK_GLAZED_TERRACOTTA','GRAY_GLAZED_TERRACOTTA','SILVER_GLAZED_TERRACOTTA','CYAN_GLAZED_TERRACOTTA','PURPLE_GLAZED_TERRACOTTA','BLUE_GLAZED_TERRACOTTA','BROWN_GLAZED_TERRACOTTA','GREEN_GLAZED_TERRACOTTA','RED_GLAZED_TERRACOTTA','BLACK_GLAZED_TERRACOTTA','CONCRETE','CONCRETE_POWDER','STRUCTURE_BLOCK','IRON_SPADE','IRON_PICKAXE','IRON_AXE','FLINT_AND_STEEL','APPLE','BOW','ARROW','COAL','DIAMOND','IRON_INGOT','GOLD_INGOT','IRON_SWORD','WOOD_SWORD','WOOD_SPADE','WOOD_PICKAXE','WOOD_AXE','STONE_SWORD','STONE_SPADE','STONE_PICKAXE','STONE_AXE','DIAMOND_SWORD','DIAMOND_SPADE','DIAMOND_PICKAXE','DIAMOND_AXE','STICK','BOWL','MUSHROOM_SOUP','GOLD_SWORD','GOLD_SPADE','GOLD_PICKAXE','GOLD_AXE','STRING','FEATHER','SULPHUR','WOOD_HOE','STONE_HOE','IRON_HOE','DIAMOND_HOE','GOLD_HOE','SEEDS','WHEAT','BREAD','LEATHER_HELMET','LEATHER_CHESTPLATE','LEATHER_LEGGINGS','LEATHER_BOOTS','CHAINMAIL_HELMET','CHAINMAIL_CHESTPLATE','CHAINMAIL_LEGGINGS','CHAINMAIL_BOOTS','IRON_HELMET','IRON_CHESTPLATE','IRON_LEGGINGS','IRON_BOOTS','DIAMOND_HELMET','DIAMOND_CHESTPLATE','DIAMOND_LEGGINGS','DIAMOND_BOOTS','GOLD_HELMET','GOLD_CHESTPLATE','GOLD_LEGGINGS','GOLD_BOOTS','FLINT','PORK','GRILLED_PORK','PAINTING','GOLDEN_APPLE','SIGN','WOOD_DOOR','BUCKET','WATER_BUCKET','LAVA_BUCKET','MINECART','SADDLE','IRON_DOOR','REDSTONE','SNOW_BALL','BOAT','LEATHER','MILK_BUCKET','CLAY_BRICK','CLAY_BALL','SUGAR_CANE','PAPER','BOOK','SLIME_BALL','STORAGE_MINECART','POWERED_MINECART','EGG','COMPASS','FISHING_ROD','WATCH','GLOWSTONE_DUST','RAW_FISH','COOKED_FISH','INK_SACK','BONE','SUGAR','CAKE','BED','DIODE','COOKIE','MAP','SHEARS','MELON','PUMPKIN_SEEDS','MELON_SEEDS','RAW_BEEF','COOKED_BEEF','RAW_CHICKEN','COOKED_CHICKEN','ROTTEN_FLESH','ENDER_PEARL','BLAZE_ROD','GHAST_TEAR','GOLD_NUGGET','NETHER_STALK','POTION','GLASS_BOTTLE','SPIDER_EYE','FERMENTED_SPIDER_EYE','BLAZE_POWDER','MAGMA_CREAM','BREWING_STAND_ITEM','CAULDRON_ITEM','EYE_OF_ENDER','SPECKLED_MELON','MONSTER_EGG','EXP_BOTTLE','FIREBALL','BOOK_AND_QUILL','WRITTEN_BOOK','EMERALD','ITEM_FRAME','FLOWER_POT_ITEM','CARROT_ITEM','POTATO_ITEM','BAKED_POTATO','POISONOUS_POTATO','EMPTY_MAP','GOLDEN_CARROT','SKULL_ITEM','CARROT_STICK','NETHER_STAR','PUMPKIN_PIE','FIREWORK','FIREWORK_CHARGE','ENCHANTED_BOOK','REDSTONE_COMPARATOR','NETHER_BRICK_ITEM','QUARTZ','EXPLOSIVE_MINECART','HOPPER_MINECART','PRISMARINE_SHARD','PRISMARINE_CRYSTALS','RABBIT','COOKED_RABBIT','RABBIT_STEW','RABBIT_FOOT','RABBIT_HIDE','ARMOR_STAND','IRON_BARDING','GOLD_BARDING','DIAMOND_BARDING','LEASH','NAME_TAG','COMMAND_MINECART','MUTTON','COOKED_MUTTON','BANNER','END_CRYSTAL','SPRUCE_DOOR_ITEM','BIRCH_DOOR_ITEM','JUNGLE_DOOR_ITEM','ACACIA_DOOR_ITEM','DARK_OAK_DOOR_ITEM','CHORUS_FRUIT','CHORUS_FRUIT_POPPED','BEETROOT','BEETROOT_SEEDS','BEETROOT_SOUP','DRAGONS_BREATH','SPLASH_POTION','SPECTRAL_ARROW','TIPPED_ARROW','LINGERING_POTION','SHIELD','ELYTRA','BOAT_SPRUCE','BOAT_BIRCH','BOAT_JUNGLE','BOAT_ACACIA','BOAT_DARK_OAK','TOTEM','SHULKER_SHELL','IRON_NUGGET','KNOWLEDGE_BOOK','GOLD_RECORD','GREEN_RECORD','RECORD_3','RECORD_4','RECORD_5','RECORD_6','RECORD_7','RECORD_8','RECORD_9','RECORD_10','RECORD_11','RECORD_12')),
  constraint pk_rc_skills_skill primary key (name_key)
);

create table rc_skills_skill_data (
  id                            integer auto_increment not null,
  skill_id                      integer,
  data_key                      varchar(255),
  data_value                    varchar(255),
  constraint pk_rc_skills_skill_data primary key (id)
);

alter table rc_skills_heroes add constraint fk_rc_skills_heroes_exp_pool_id foreign key (exp_pool_id) references rc_skills_exp_pool (id) on delete restrict on update restrict;

create index ix_rc_skills_attributes_hero_id on rc_skills_attributes (hero_id);
alter table rc_skills_attributes add constraint fk_rc_skills_attributes_hero_id foreign key (hero_id) references rc_skills_heroes (id) on delete restrict on update restrict;

create index ix_rc_skills_hero_options_hero_id on rc_skills_hero_options (hero_id);
alter table rc_skills_hero_options add constraint fk_rc_skills_hero_options_hero_id foreign key (hero_id) references rc_skills_heroes (id) on delete restrict on update restrict;

create index ix_rc_skills_hero_professions_hero_id on rc_skills_hero_professions (hero_id);
alter table rc_skills_hero_professions add constraint fk_rc_skills_hero_professions_hero_id foreign key (hero_id) references rc_skills_heroes (id) on delete restrict on update restrict;

create index ix_rc_skills_hero_resources_profession_id on rc_skills_hero_resources (profession_id);
alter table rc_skills_hero_resources add constraint fk_rc_skills_hero_resources_profession_id foreign key (profession_id) references rc_skills_hero_professions (id) on delete restrict on update restrict;

create index ix_rc_skills_hero_skills_profession_id on rc_skills_hero_skills (profession_id);
alter table rc_skills_hero_skills add constraint fk_rc_skills_hero_skills_profession_id foreign key (profession_id) references rc_skills_hero_professions (id) on delete restrict on update restrict;

create index ix_rc_skills_hero_skills_hero_id on rc_skills_hero_skills (hero_id);
alter table rc_skills_hero_skills add constraint fk_rc_skills_hero_skills_hero_id foreign key (hero_id) references rc_skills_heroes (id) on delete restrict on update restrict;

create index ix_rc_skills_profession_parent_name_key on rc_skills_profession (parent_name_key);
alter table rc_skills_profession add constraint fk_rc_skills_profession_parent_name_key foreign key (parent_name_key) references rc_skills_profession (name_key) on delete restrict on update restrict;

create index ix_rc_skills_skill_profession_name_key on rc_skills_skill (profession_name_key);
alter table rc_skills_skill add constraint fk_rc_skills_skill_profession_name_key foreign key (profession_name_key) references rc_skills_profession (name_key) on delete restrict on update restrict;

create index ix_rc_skills_skill_data_skill_id on rc_skills_skill_data (skill_id);
alter table rc_skills_skill_data add constraint fk_rc_skills_skill_data_skill_id foreign key (skill_id) references rc_skills_hero_skills (id) on delete restrict on update restrict;

