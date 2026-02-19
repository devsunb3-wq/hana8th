package com.hana8.hello.sol;

import com.hana8.hello.Calc;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalcTest {
	@Test
	void add_return_sum() {
		Calc cal = new Calc(2, 3);

		int result = cal.add();
		assertEquals(5, result);
	}

}
