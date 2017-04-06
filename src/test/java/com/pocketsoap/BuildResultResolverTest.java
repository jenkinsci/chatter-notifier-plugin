package com.pocketsoap;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jharringa on 4/4/17.
 */
public class BuildResultResolverTest {

    private AbstractBuild build;
    private AbstractBuild previousBuild;

    @Before
    public void setUp() {
        build = mock(AbstractBuild.class);
        previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
    }

    @Test
    public void testNullPreviousBuildReturnsCurrentBuildResult() {
        when(build.getPreviousBuild()).thenReturn(null);
        when(build.getResult()).thenReturn(Result.SUCCESS);

        String actualResult = BuildResultResolver.getContextualResult(build);

        assertEquals(Result.SUCCESS.toString(), actualResult);
    }

    @Test
    public void testPreviousBuildResultNotFailureSoReturnCurrentStatus() {
        when(previousBuild.getResult()).thenReturn(Result.ABORTED);
        when(build.getResult()).thenReturn(Result.SUCCESS);

        String actualResult = BuildResultResolver.getContextualResult(build);

        assertEquals(Result.SUCCESS.toString(), actualResult);
    }

    @Test
    public void testPreviousBuildFailedAndThisBuildSucceededSoFixed() {
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);
        when(build.getResult()).thenReturn(Result.SUCCESS);

        String actualResult = BuildResultResolver.getContextualResult(build);

        assertEquals(BuildResultResolver.FIXED_STATUS, actualResult);
    }

    @Test
    public void testPreviousBuildFailedAndThisBuildFailedSoStillFailing() {
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);
        when(build.getResult()).thenReturn(Result.FAILURE);

        String actualResult = BuildResultResolver.getContextualResult(build);

        assertEquals(BuildResultResolver.STILL_FAILING_STATUS, actualResult);
    }
}