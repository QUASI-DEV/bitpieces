package com.heretic.bitpieces_practice.tables;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

public class Tables {

	@Table("users_required_fields") 
	public static class Users_required_fields extends Model {}

	@Table("users") 
	public static class User extends Model {}

	@Table("creators") 
	public static class Creator extends Model {}

	@Table("creators_required_fields") 
	public static class Creators_required_fields extends Model {}

	@Table("users_btc_addresses") 
	public static class Users_btc_address extends Model {}

	@Table("creators_btc_addresses") 
	public static class Creators_btc_address extends Model {}

	@Table("pieces_issued") 
	public static class Pieces_issued extends Model {}

	@Table("pieces_owned") 
	public static class Pieces_owned extends Model {}
	
	@Table("pieces_total") 
	public static class Pieces_total extends Model {}

	@Table("bids") 
	public static class Bid extends Model {}

	@Table("asks") 
	public static class Ask extends Model {}

	@Table("sales") 
	public static class Sale extends Model {}

	@Table("rewards") 
	public static class Reward extends Model {}

	@Table("Rewards_earned") 
	public static class Rewards_earned extends Model {}

}

