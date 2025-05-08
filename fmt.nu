#!bin/nu

print "\nFormatting...\n"

let files = fd | parse '{name}' | $in.name | filter { |file| $file | str contains ".java" }

for file in $files {
	const TABS = "\t"
	const SPACES = "    "

	mut text = open $file

	$text = tabify $text
	$text = varify $text

	let text = cat $file | str replace $SPACES $TABS --all
	[$text, "\n"] | str join | save -f $file
}

def tabify [text:string] {
	const TABS = "\t"
	const SPACES = "    "

	$text | str replace $SPACES $TABS --all
}

def varify [text:string] {
	const VARIABLE = ".* .* = .*;"
	const NOT_VARIABLE = "public|private|static|for"
	const VAR_VARIABLE = "var .* = .*;"

	let lines = $text | lines | each {|line| if (
			$line =~ $VARIABLE 
			and $line !~ $NOT_VARIABLE 
			and $line !~ $VAR_VARIABLE
		) {
			# TODO 
			$line
		} else {
			$line
		}
	}

	$lines | str join
}
