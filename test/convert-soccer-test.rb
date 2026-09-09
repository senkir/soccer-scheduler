require 'test/unit'
require_relative '../convert-soccer.rb'

class ConvertSoccerTest < Test::Unit::TestCase

	@@line_a = "9/6/2025,	10:30,	WSC-G6B-Okamoto,	Powerlines Park 4"
	@@line_b = "	H	Sep 6, 2025, Sat	10:30 a.m.	WSC-G6B-Okamoto	Powerlines Park 4"

	@@target = Converter.new

	def test_parse_line
		out = @@target.parse_line(line: @@line_b)
		assert_equal("09/06/2025, 10:30, WSC-G6B-Okamoto, Powerlines Park 21200 NW Rock Creek", out)
	end

	def test_parse_date
		out = @@target.parse_date( date: "Sep 6, 2025, Sat")
		assert_equal("09/06/2025", out)
	end

	def test_parse_time
		out = @@target.parse_time( time: "10:30 a.m.")
		assert_equal("10:30", out)
	end

	def test_location
		out = @@target.parse_location( location: "Powerlines Park 4")
		assert_equal("Powerlines Park 21200 NW Rock Creek", out)
	end
end
