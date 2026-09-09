#!/usr/bin/env ruby

require 'date'
require 'optparse'

class Converter

  FIELDS_HASH = {
    "Powerlines Park": "Powerlines Park 21200 NW Rock Creek",
    "Elmonica Elem": "Elmonica Elem 16950 SW Lisa St",
    "Cook Park":  "Cook Park 1 16638 SW 92nd Avenue",
    "Water District Park": "1751 SW Merlo Dr, Beaverton, OR 97006",
    "Atfalati Park": "6600 SW Sagert St, Tualatin, OR 97062",
    "Conestoga": "Conestoga 12250 SW Conestoga Dr",
    "Mt View Champions Park": "5915 SW 170th Ave, Beaverton, OR 97007",
    "Jurgens Park": "17255 SW Jurgens Ave, Tualatin, OR 97062",
    "Hawks View Elem": "21970 Southwest Sherwood Boulevard, Sherwood, OR 97140",
    "Sherwood HS NW": "18800 SW Haide Rd, Sherwood, OR 97140"
  }

  attr_accessor :errors
  attr_accessor :child

  def initialize
    @errors = []
  end

  def _print_errors
    puts "\nErrors:\n"
    puts "------\n"
    puts @errors
  end

  def print(file:)
    out = parse(file: file)
    puts  "\nParsed Soccer Output\n"
    puts "------\n"
    puts out
    _print_errors
  end

  def print_from_string raw_input
    puts = "Parsed Soccer Output:\n\n"
    puts "------\n"
    puts _string_parse_internal(raw_input: raw_input)
    _print_errors
  end

  def parse(file:)
    out_lines = []
    out_lines << "Start Date, Start Time,  Description, Location, Subject,"
    File.open(file).each do |line|
      out_lines << "#{parse_line(line: line)}"
    end
    out_lines
  end

  def _string_parse_internal(raw_input:)
    out_lines = []
    out_lines << "Start Date, Start Time,  Description, Location, Subject,"
    raw_input.split("\n").each do |line|
      #print "#{line_num +=1}"
      out_lines << "#{parse_line(line: line)}"
    end
    out_lines
  end

  def parse_line(line:)
    sections = line.strip.split("\t")

    is_error = false

    out_string = ""
    #date
    begin
    out_string << parse_date(date: sections[1])
    out_string << ", "
    #time
    out_string << parse_time(time: sections[2])
    out_string << ", "
    #description
    out_string << sections[3]
    if (sections[3] == "(BYE)")
      @errors << "ERROR: 'BUY' on date #{parse_date(date: sections[1])}"
      is_error = true
    end
    out_string << ", "
    #location
    out_string << parse_location(location: sections[4])
    out_string << ", "
    #subject
    out_string << "#{@child} Soccer Game - #{sections[4]}"

    rescue => e
      @errors << "ERROR: date parse error #{e} \n raw input #{sections}"
    end
    return "" if is_error
    out_string
  end

  #format Sep 6, 2025, Sat
  def parse_date(date:)
    DateTime.parse(date).strftime("%m/%d/%Y")
  end

  #format Powerlines Park 4
  def parse_location(location:)
    sanitized_loc = location[/^[a-zA-Z ]*/].strip
    out = FIELDS_HASH[:"#{sanitized_loc}"]
    if (out != nil)
      out
    else
      @errors << "ERROR: unknown location: #{location}"
      sanitized_loc
    end
  end

  #format 10:30 a.m.
  def parse_time(time:)
    digits = time[/^[0-9:]*/]
    shifter = time[/[ap].[m]./]
    if (shifter == "p.m.")
      hours = digits.split(":")[0]
      minutes = digits.split(":")[1]
      "#{hours.to_i + 12}:#{minutes}"
    else
      digits
    end
  end
end

file = ARGV[0]

return if (ARGV.length == 0)
convert = Converter.new

options = {}
OptionParser.new do |opts|
  opts.banner = "Usage: example"
  opts.on("-c", "--child [CHILD]", String) do |c|
    convert.child = c
  end
  opts.on("-f", "--file [FILE]", String) do |f|
  convert.print(file: f)
  exit
  end
end.parse!

raw_string = ARGV.pop
puts "raw string #{raw_string}"
puts convert.print_from_string raw_string

# convert = Converter.new

# if (options[:file] != nil)
#   convert.print(file: options[:file])
# else
#   convert.string_parse ARGV[0]
# end
