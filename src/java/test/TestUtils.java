package test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class TestUtils
{
	public static <T> void test(Class<T> tester)
	{
		Arrays.stream(tester.getMethods())
			.filter(method -> Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(Test.class))
			.filter(method -> method.getAnnotation(Test.class).active())
			.forEach(method ->
			{
				Test info = method.getAnnotation(Test.class);
				test(method, info.benchmark());
			});
	}

	public static void test(Method method, boolean benchmark)
	{
		System.out.printf("Testing %s\n", method.getName());

		try
		{
			long start = System.currentTimeMillis();
			method.invoke(null);
			long end = System.currentTimeMillis();
			System.out.print("Testing complete");
			if (benchmark) System.out.printf(", took %dms", end - start);
			System.out.println();
		} catch (Exception e)
		{
			System.out.println();
			e.printStackTrace();
		}
	}
}
