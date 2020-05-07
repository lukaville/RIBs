package com.badoo.ribs.example.rib.switcher

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.builder.BuildContext
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.configuration.feature.operation.push
import com.badoo.ribs.example.rib.blocker.BlockerView
import com.badoo.ribs.example.rib.dialog_example.DialogExampleNode
import com.badoo.ribs.example.rib.foo_bar.FooBarNode
import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.hello_world.HelloWorldNode
import com.badoo.ribs.example.rib.menu.MenuNode
import com.badoo.ribs.example.rib.switcher.subtree.SwitcherRouter
import com.badoo.ribs.example.rib.switcher.subtree.Configuration.Content
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.invocation.InvocationOnMock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SwitcherWorkflowTest {

    private lateinit var workflow: Switcher
    private lateinit var router: SwitcherRouter
    private lateinit var interactor: SwitcherInteractor

    @Before
    fun setup() {
        val helloWorldNodeBuilder = { buildContext: BuildContext ->
            HelloWorldNode(mock(), mock(), mock(), buildContext.toBuildParams()) { listOf(
                mock(),
                mock()
            ) }
        }
        val fooBarNodeBuilder = { buildContext: BuildContext ->
            FooBarNode(mock(), mock(), buildContext.toBuildParams(), emptySet())
        }
        val dialogExampleBuilder = { buildContext: BuildContext ->
            DialogExampleNode(buildContext.toBuildParams(), mock()) { listOf(mock(), mock()) }
        }
        val blockerBuilder = { buildContext: BuildContext ->
            Node<BlockerView>(buildContext.toBuildParams(), mock(), mock(), mock(), mock())
        }
        val menuBuilder = { buildContext: BuildContext ->
            MenuNode(buildContext.toBuildParams(), mock(), mock())
        }

        router = SwitcherRouter(
            transitionHandler = null,
            buildParams = BuildParams.Empty(),
            fooBarBuilder = mock { on { build(any()) } doAnswer (withBuilder(fooBarNodeBuilder)) },
            helloWorldBuilder = mock {
                on { build(any()) } doAnswer (withBuilder(
                    helloWorldNodeBuilder
                ))
            },
            dialogExampleBuilder = mock {
                on { build(any()) } doAnswer (withBuilder(
                    dialogExampleBuilder
                ))
            },
            blockerBuilder = mock { on { build(any()) } doAnswer (withBuilder(blockerBuilder)) },
            menuBuilder = mock { on { build(any()) } doAnswer (withBuilder(menuBuilder)) },
            dialogLauncher = mock(),
            dialogToTestOverlay = mock()
        )
        interactor = SwitcherInteractor(BuildParams.Empty(), mock(), mock())

        workflow = SwitcherNode(
            buildParams = BuildParams.Empty(),
            viewFactory = mock(),
            plugins = listOf(interactor, router)
        ).also { it.onAttach() }
    }

    private fun <N> withBuilder(
            builder: (BuildContext) -> N
    ): (InvocationOnMock) -> N = { answer -> builder(answer.getArgument(0)) }

    private fun BuildContext.toBuildParams(): BuildParams<Nothing?> =
        BuildParams(
            payload = null,
            buildContext = this
        )

    @Test
    fun `attachHelloWorld`() {
        val testObserver = TestObserver<HelloWorld>()

        workflow.attachHelloWorld().subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertComplete()
    }

    @Test
    fun `testCrash`() {
        val testObserver = TestObserver<HelloWorld>()

        workflow.testCrash().subscribe(testObserver)

        testObserver.assertError(Throwable::class.java)
    }

    @Test
    fun `waitForHelloWorld - hello is already attached`() {
        val testObserver = TestObserver<HelloWorld>()

        router.push(Content.Hello)
        workflow.waitForHelloWorld().subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertComplete()
    }

    @Test
    fun `waitForHelloWorld - hello is attached after`() {
        val testObserver = TestObserver<HelloWorld>()

        workflow.waitForHelloWorld().subscribe(testObserver)
        router.push(Content.Hello)

        testObserver.assertValueCount(1)
        testObserver.assertComplete()
    }

    @Test
    fun `waitForHelloWorld - hello is not attached`() {
        val testObserver = TestObserver<HelloWorld>()

        workflow.waitForHelloWorld().subscribe(testObserver)

        testObserver.assertValueCount(0)
        testObserver.assertNotComplete()
    }

    @Test
    fun `doSomethingAndStayOnThisNode`() {
        val testObserver = TestObserver<Switcher>()

        workflow.doSomethingAndStayOnThisNode().subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertComplete()
    }
}
