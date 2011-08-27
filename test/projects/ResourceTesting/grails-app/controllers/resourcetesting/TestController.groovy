package resourcetesting

class TestController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [testInstanceList: Test.list(params), testInstanceTotal: Test.count()]
    }

    def create = {
        def testInstance = new Test()
        testInstance.properties = params
        return [testInstance: testInstance]
    }

    def save = {
        def testInstance = new Test(params)
        if (testInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'test.label', default: 'Test'), testInstance.id])}"
            redirect(action: "show", id: testInstance.id)
        }
        else {
            render(view: "create", model: [testInstance: testInstance])
        }
    }

    def show = {
        def testInstance = Test.get(params.id)
        if (!testInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'test.label', default: 'Test'), params.id])}"
            redirect(action: "list")
        }
        else {
            [testInstance: testInstance]
        }
    }

    def edit = {
        def testInstance = Test.get(params.id)
        if (!testInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'test.label', default: 'Test'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [testInstance: testInstance]
        }
    }

    def update = {
        def testInstance = Test.get(params.id)
        if (testInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (testInstance.version > version) {
                    
                    testInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'test.label', default: 'Test')] as Object[], "Another user has updated this Test while you were editing")
                    render(view: "edit", model: [testInstance: testInstance])
                    return
                }
            }
            testInstance.properties = params
            if (!testInstance.hasErrors() && testInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'test.label', default: 'Test'), testInstance.id])}"
                redirect(action: "show", id: testInstance.id)
            }
            else {
                render(view: "edit", model: [testInstance: testInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'test.label', default: 'Test'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def testInstance = Test.get(params.id)
        if (testInstance) {
            try {
                testInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'test.label', default: 'Test'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'test.label', default: 'Test'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'test.label', default: 'Test'), params.id])}"
            redirect(action: "list")
        }
    }
}
