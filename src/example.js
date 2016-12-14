
(function() {

    var i = 1;
    example.print("Initial value of i: " + i);

    example.captureContinuation();

    i = i + 1;
    example.print("Final value of i: " + i);

})();