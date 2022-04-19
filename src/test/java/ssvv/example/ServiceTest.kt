package ssvv.example

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import validation.*
import repository.*
import domain.*
import org.junit.Assert
import service.*
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

internal class ServiceTest {

    private lateinit var service: Service

    companion object {
        private const val studentFilePath = "/Users/bogdansimion/facultate/PDP/MaxPointsParticipants/studenti.xml"
        private const val temaFilePath = "/Users/bogdansimion/facultate/PDP/MaxPointsParticipants/teme.xml"
        private const val notaFilePath = "/Users/bogdansimion/facultate/PDP/MaxPointsParticipants/note.xml"
    }

    @BeforeEach
    fun setUp() {
        val xmlInit = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><inbox></inbox>"


        Files.write(
                Paths.get(studentFilePath),
                Collections.singletonList(xmlInit),
                StandardCharsets.UTF_8
        )
        Files.write(
                Paths.get(temaFilePath),
                Collections.singletonList(xmlInit),
                StandardCharsets.UTF_8
        )
        Files.write(
                Paths.get(notaFilePath),
                Collections.singletonList(xmlInit),
                StandardCharsets.UTF_8
        )

        val studentXMLRepository = StudentXMLRepository(StudentValidator(), studentFilePath)
        val temaXMLRepository = TemaXMLRepository(TemaValidator(), temaFilePath)
        val notaXMLRepository = NotaXMLRepository(NotaValidator(), notaFilePath)

        service = Service(
                studentXMLRepository,
                temaXMLRepository,
                notaXMLRepository,
        )
    }

    @AfterEach
    @Throws(IOException::class)
    fun tearDown() {

    }

    @org.junit.Test
    fun saveStudent() {
        val studentValidator: Validator<Student> = StudentValidator()
        val temaValidator: Validator<Tema> = TemaValidator()
        val notaValidator: Validator<Nota> = NotaValidator()
        val fileRepository1 = StudentXMLRepository(studentValidator, "studenti.xml")
        val fileRepository2 = TemaXMLRepository(temaValidator, "teme.xml")
        val fileRepository3 = NotaXMLRepository(notaValidator, "note.xml")
        val service: Service = Service(
                fileRepository1,
                fileRepository2,
                fileRepository3,
        )
        Assert.assertEquals(service.saveStudent("01", "nume1", 1).toLong(), 1)
    }

    @org.junit.Test
    fun updateStudent() {
        val studentValidator: validation.Validator<Student> = StudentValidator()
        val temaValidator: validation.Validator<Tema> = TemaValidator()
        val notaValidator: validation.Validator<Nota> = NotaValidator()
        val fileRepository1 = StudentXMLRepository(studentValidator, "studenti.xml")
        val fileRepository2 = TemaXMLRepository(temaValidator, "teme.xml")
        val fileRepository3 = NotaXMLRepository(notaValidator, "note.xml")
        val service: service.Service = Service(fileRepository1, fileRepository2, fileRepository3)
        org.junit.Assert.assertEquals(service.saveStudent("01", "nume1", 1).toLong(), 1)
        org.junit.Assert.assertEquals(service.updateStudent("01", "nume12", 1).toLong(), 0)
    }
    @Test
    fun addStudent_validStudent_addsTheStudent() {
        try {
            service.saveStudent("123", "boalfa", 112)
            Assertions.assertEquals(service.findAllStudents().toList().size, 1)
        } catch (_: Exception) {
        }
    }

    @Test
    fun addStudent_invalidStudent_SameID_DoesNotAddTheStudent() {
        try {
            service.saveStudent("129", "bo", 112)
            service.saveStudent("129", "bo", 112)
        }
        catch (_: Exception) {
        }
        Assertions.assertEquals(service.findAllStudents().toList().size, 1)
    }

    @Test
    fun addStudent_invalidStudentName_doesntAddTheStudent() {
        try {
            service.saveStudent("123", "", 112)
        } catch (_: Exception) {
        }
        Assertions.assertEquals(service.findAllStudents().toList().size, 0)
    }

    @Test
    fun addStudent_invalidStudentId_doesntAddTheStudent() {
        try {
            service.saveStudent("", "boalfa", 112)
        } catch (_: Exception) {
        }
        Assertions.assertEquals(service.findAllStudents().toList().size, 0)
    }

    @Test
    fun addStudent_invalidStudentGroupLessThan110_doesntAddTheStudent() {
        try {
            service.saveStudent("123", "boalfa", 109)
        } catch (_: Exception) {
        }
        Assertions.assertEquals(service.findAllStudents().toList().size, 0)
    }

    @Test
    fun addStudent_invalidStudentGroupBiggerThan938_doesntAddTheStudent() {
        try {
            service.saveStudent("123", "boalfa", 1000)
        } catch (_: Exception) {
        }
        Assertions.assertEquals(service.findAllStudents().toList().size, 0)
    }

    @Test
    fun addAssignment_validAssignment_addsTheAssignment() {
        try {
            service.saveTema("123", "123", 2, 1)
            Assertions.assertEquals(service.findAllTeme().toList().size, 1)
        } catch (_: Exception) {
            Assertions.fail()
        }
    }

    @Test
    fun addAssignment_invalidAssignmentId_doesntAddTheAssignment() {
        try {
            service.saveTema("", "123", 2, 1)
            throw Exception()
        } catch (_: Exception) {
            Assertions.assertEquals(service.findAllTeme().toList().size, 0)
        }
    }

    @Test
    fun addAssignment_invalidAssignmentDescriere_doesntAddTheAssignment() {
        try {
            service.saveTema("123", "", 2, 1)
            throw Exception()
        } catch (_: Exception) {
            Assertions.assertEquals(service.findAllNote().toList().size, 0)
        }
    }

    @Test
    fun addAssignment_invalidAssignmentDeadline_doesntAddTheAssignment() {
        try {
            service.saveTema("123", "123", -1, 2)
            throw Exception()
        } catch (_: Exception) {
            Assertions.assertEquals(service.findAllNote().toList().size, 0)
        }
    }

    @Test
    fun addAssignment_invalidAssignmentStartline_doesntAddTheAssignment() {
        try {
            service.saveTema("123", "", 1, -1)
            throw Exception()
        } catch (_: java.lang.Exception) {
            Assertions.assertEquals(service.findAllNote().toList().size, 0)
        }
    }

    @Test
    fun addAssignment_invalidAssignmentCombinationOfDeadlineStartline_doesntAddTheAssignment() {
        try {
            service.saveTema("123", "", 1, 2)
            throw Exception()
        } catch (_: java.lang.Exception) {
            Assertions.assertEquals(service.findAllNote().toList().size, 0)
        }
    }

    @Test
    fun addGrade_validGrade_addsTheGrade() {
        try {
            service.saveTema("123", "123", 2, 2)
            service.saveStudent("123", "boalfa", 112)
            service.saveNota("123", "123", 7.0, 1, "Da")

            Assertions.assertEquals(service.findAllNote().toList().size, 1)
        } catch (_: Exception) {
            Assertions.fail()
        }
    }
}

