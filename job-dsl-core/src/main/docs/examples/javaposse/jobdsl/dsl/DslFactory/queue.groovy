def example1 = job('example-1') {
    displayName('first example')
}

queue(example1)

job('example-2') {
    displayName('second example')
}

queue('example-2')
