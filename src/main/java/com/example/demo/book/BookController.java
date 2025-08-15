package com.example.demo.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book API", description = "API for managing books") // Add a tag for the controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Operation(summary = "Get all books", description = "Returns a list of all books") // Document the getAllBooks operation
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Operation(summary = "Get book by ID", description = "Returns a single book by its ID") // Document getBookById
    @ApiResponse(responseCode = "200", description = "Successfully retrieved book",
            content = @Content(schema = @Schema(implementation = Book.class))) // Document successful response
    @ApiResponse(responseCode = "404", description = "Book not found") // Document not found response
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new book", description = "Creates a new book") // Document createBook
    @ApiResponse(responseCode = "201", description = "Book created successfully",
            content = @Content(schema = @Schema(implementation = Book.class))) // Document created response
    @RequestBody(description = "Book object to be created", required = true,
            content = @Content(schema = @Schema(implementation = Book.class))) // Document request body
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @Operation(summary = "Update an existing book", description = "Updates an existing book by its ID") // Document updateBook
    @ApiResponse(responseCode = "200", description = "Book updated successfully",
            content = @Content(schema = @Schema(implementation = Book.class))) // Document successful response
    @ApiResponse(responseCode = "404", description = "Book not found") // Document not found response
    @RequestBody(description = "Updated book object", required = true,
            content = @Content(schema = @Schema(implementation = Book.class))) // Document request body
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            Book existingBook = book.get();
            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setAuthor(bookDetails.getAuthor());
            existingBook.setIsbn(bookDetails.getIsbn());
            bookRepository.save(existingBook);
            return ResponseEntity.ok(existingBook);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a book", description = "Deletes a book by its ID") // Document deleteBook
    @ApiResponse(responseCode = "204", description = "Book deleted successfully") // Document successful deletion
    @ApiResponse(responseCode = "404", description = "Book not found") // Document not found response
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
